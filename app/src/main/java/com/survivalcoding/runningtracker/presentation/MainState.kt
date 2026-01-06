package com.survivalcoding.runningtracker.presentation

import androidx.compose.runtime.Stable
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.model.SortType
import com.survivalcoding.runningtracker.domain.model.GpsStatus
import com.survivalcoding.runningtracker.presentation.service.TrackingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class MainState(
    val runs: ImmutableList<Run> = persistentListOf(),
    val sortType: SortType = SortType.DATE,
    val gpsStatus: GpsStatus = GpsStatus.Acquired,
    val trackingState: TrackingState = TrackingState(),
    val selectedRun: Run? = null,
    val displayPathPoints: ImmutableList<LocationPoint> = persistentListOf(),
    val totalDistanceInMeters: Double = 0.0,
    val totalTimeInMillis: Long = 0L,
    val totalAvgSpeedInKMH: Float = 0f,
    val totalCaloriesBurned: Int = 0,
    val isGpsMockingEnabled: Boolean = false,
) {
    val isTracking: Boolean get() = trackingState.isTracking
}