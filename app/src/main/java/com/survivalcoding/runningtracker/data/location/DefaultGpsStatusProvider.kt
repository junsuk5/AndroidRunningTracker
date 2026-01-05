package com.survivalcoding.runningtracker.data.location

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.GnssStatus
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.survivalcoding.runningtracker.domain.location.GpsStatusProvider
import com.survivalcoding.runningtracker.domain.model.GpsStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class DefaultGpsStatusProvider(
    private val context: Context,
) : GpsStatusProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun observeGpsStatus(): Flow<GpsStatus> = callbackFlow {

        val checkStatus = {
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (!isGpsEnabled) {
                trySend(GpsStatus.Disabled)
            } else {
                // Enabled 상태로 시작하고, GnssStatus를 통해 Acquired를 판단
                trySend(GpsStatus.Enabled)
            }
        }

        // 초기 상태 체크
        checkStatus()

        // 1. GNSS Status 감지 (Seeking/Connected 대응)
        val gnssStatusCallback = object : GnssStatus.Callback() {
            override fun onStarted() {
                trySend(GpsStatus.Enabled)
            }

            override fun onFirstFix(ttffMillis: Int) {
                trySend(GpsStatus.Acquired)
            }

            override fun onStopped() {
                checkStatus()
            }
        }

        // 2. GPS 엔진 Warm-up (FusedLocationProviderClient)
        val locationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .build()
        val locationCallback = object : LocationCallback() {}

        try {
            locationManager.registerGnssStatusCallback(gnssStatusCallback, null)
            locationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            trySend(GpsStatus.Disabled)
        } catch (e: Exception) {
            trySend(GpsStatus.Disabled)
        }

        awaitClose {
            locationManager.unregisterGnssStatusCallback(gnssStatusCallback)
            locationClient.removeLocationUpdates(locationCallback)
        }
    }.distinctUntilChanged()
}
