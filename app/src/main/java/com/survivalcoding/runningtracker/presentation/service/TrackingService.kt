package com.survivalcoding.runningtracker.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.survivalcoding.runningtracker.MainActivity
import com.survivalcoding.runningtracker.R
import com.survivalcoding.runningtracker.core.util.TrackingCalculator
import com.survivalcoding.runningtracker.domain.battery.BatteryLevelProvider
import com.survivalcoding.runningtracker.domain.location.LocationClient
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.use_case.SaveRunUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TrackingService : LifecycleService() {

    private val trackingManager: TrackingManager by inject()
    private val locationClient: LocationClient by inject()
    private val batteryLevelProvider: BatteryLevelProvider by inject()
    private val saveRunUseCase: SaveRunUseCase by inject()

    private var locationJob: Job? = null
    private var timerJob: Job? = null
    private var batteryJob: Job? = null
    private var timeStarted = 0L
    private var lapTime = 0L
    private var totalTime = 0L

    private var lastLocation: LocationPoint? = null
    private var distanceInMeters = 0.0

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundService()
            ACTION_STOP -> stopForegroundService()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // TODO: 적절한 아이콘으로 교체 필요
            .setContentTitle("Running Tracker")
            .setContentText("운동 중...")
            .setContentIntent(pendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)
        trackingManager.updateTrackingState(true)
        startLocationTracking()
        startTimer()
        startBatteryMonitoring()
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = lifecycleScope.launch(Dispatchers.Main) {
            while (true) {
                lapTime = System.currentTimeMillis() - timeStarted
                trackingManager.updateTime(totalTime + lapTime)
                delay(50L)
            }
        }
    }

    private fun startLocationTracking() {
        locationJob?.cancel()
        locationJob = locationClient.getLocationUpdates(1000L) // 1초 간격
            .onEach { point ->
                lastLocation?.let { last ->
                    val distance = TrackingCalculator.calculateDistance(
                        last.latitude, last.longitude,
                        point.latitude, point.longitude
                    )
                    distanceInMeters += distance
                    trackingManager.updateDistance(distanceInMeters)

                    // Calculate average speed in KMH
                    val speedInKMH = TrackingCalculator.calculateAvgSpeed(
                        distanceInMeters,
                        totalTime + lapTime
                    )
                    trackingManager.updateAvgSpeed(speedInKMH)
                }
                lastLocation = point
                trackingManager.addPathPoint(point)
            }
            .launchIn(lifecycleScope)
    }

    private var hasShownBatteryWarning = false

    private fun startBatteryMonitoring() {
        batteryJob?.cancel()
        batteryJob = batteryLevelProvider.getBatteryLevel()
            .onEach { level ->
                if (level <= 20) {
                    finishAndSaveRun(level)
                    batteryJob?.cancel()
                } else if (level <= 30 && !hasShownBatteryWarning) {
                    showBatteryWarningNotification(level)
                    hasShownBatteryWarning = true
                } else if (level > 30) {
                    hasShownBatteryWarning = false
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun finishAndSaveRun(level: Int) {
        lifecycleScope.launch {
            val trackingState = trackingManager.state.value
            val run = Run(
                distanceInMeters = trackingState.distanceInMeters,
                timeInMillis = trackingState.timeInMillis,
                timestamp = System.currentTimeMillis(),
                avgSpeedInKMH = trackingState.avgSpeedInKMH,
                caloriesBurned = trackingState.caloriesBurned,
                pathPoints = trackingState.pathPoints
            )
            saveRunUseCase(run)

            showAutoStopNotification(level)
            stopForegroundService()
        }
    }

    private fun showAutoStopNotification(level: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            2,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("배터리 부족으로 자동 종료")
            .setContentText("배터리 잔량이 ${level}% 이하로 떨어져 기록을 저장하고 종료했습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(AUTO_STOP_NOTIFICATION_ID, notification)
    }

    private fun showBatteryWarningNotification(level: Int) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            1, // Different requestCode to avoid conflict with tracking notification
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("배터리 부족 경고")
            .setContentText("배터리 잔량이 ${level}% 이하입니다. 운동 기록이 중단될 수 있습니다.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(BATTERY_NOTIFICATION_ID, notification)
    }

    private fun stopForegroundService() {
        locationJob?.cancel()
        timerJob?.cancel()
        batteryJob?.cancel()
        totalTime += lapTime
        trackingManager.updateTrackingState(false)
        totalTime = 0L
        lapTime = 0L
        lastLocation = null
        distanceInMeters = 0.0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 일반 트래킹용 채널 (조용히 유지)
            val trackingChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(trackingChannel)

            // 경고/알림용 채널 (소리 및 팝업 활성화)
            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                ALERT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_CHANNEL_ID = "tracking_channel_v1"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val ALERT_CHANNEL_ID = "alert_channel_v1"
        const val ALERT_CHANNEL_NAME = "Alerts"
        const val NOTIFICATION_ID = 1
        const val BATTERY_NOTIFICATION_ID = 2
        const val AUTO_STOP_NOTIFICATION_ID = 3
    }
}
