package com.survivalcoding.runningtracker.presentation.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.location.Location
import com.survivalcoding.runningtracker.MainActivity
import com.survivalcoding.runningtracker.R
import com.survivalcoding.runningtracker.domain.location.LocationClient
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class TrackingService : Service() {

    private val trackingManager: TrackingManager by inject()
    private val locationClient: LocationClient by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationJob: Job? = null
    private var timerJob: Job? = null
    private var timeStarted = 0L
    private var lapTime = 0L
    private var totalTime = 0L

    private var lastLocation: LocationPoint? = null
    private var distanceInMeters = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startForegroundService()
            ACTION_STOP -> stopForegroundService()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    private fun startForegroundService() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
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
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        timerJob?.cancel()
        timerJob = serviceScope.launch(Dispatchers.Main) {
            while (true) {
                lapTime = System.currentTimeMillis() - timeStarted
                trackingManager.updateTime(totalTime + lapTime)
                kotlinx.coroutines.delay(50L)
            }
        }
    }

    private fun startLocationTracking() {
        locationJob?.cancel()
        locationJob = locationClient.getLocationUpdates(1000L) // 1초 간격
            .onEach { point ->
                lastLocation?.let { last ->
                    val distance = FloatArray(1)
                    Location.distanceBetween(
                        last.latitude, last.longitude,
                        point.latitude, point.longitude,
                        distance
                    )
                    distanceInMeters += distance[0].toInt()
                    trackingManager.updateDistance(distanceInMeters)

                    // Calculate average speed in KMH
                    val totalTimeInSeconds = (totalTime + lapTime) / 1000f
                    if (totalTimeInSeconds > 0) {
                        val speedInKMH = (distanceInMeters / totalTimeInSeconds) * 3.6f
                        trackingManager.updateAvgSpeed(speedInKMH)
                    }
                }
                lastLocation = point
                trackingManager.addPathPoint(point)
            }
            .launchIn(serviceScope)
    }

    private fun stopForegroundService() {
        locationJob?.cancel()
        timerJob?.cancel()
        totalTime += lapTime
        trackingManager.updateTrackingState(false)
        totalTime = 0L
        lapTime = 0L
        lastLocation = null
        distanceInMeters = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
        const val NOTIFICATION_CHANNEL_NAME = "Tracking"
        const val NOTIFICATION_ID = 1
    }
}
