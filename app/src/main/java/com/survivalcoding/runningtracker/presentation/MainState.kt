package com.survivalcoding.runningtracker.presentation

import androidx.compose.runtime.Stable
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.presentation.service.TrackingState

@Stable
data class MainState(
    val runs: List<Run> = emptyList(),
    val sortType: SortType = SortType.DATE,
    val trackingState: TrackingState = TrackingState(),
    val totalDistanceInMeters: Int = 0,
    val totalTimeInMillis: Long = 0L,
    val totalAvgSpeedInKMH: Float = 0f,
    val totalCaloriesBurned: Int = 0
) {
    val isTracking: Boolean get() = trackingState.isTracking
    val pathPoints: List<LocationPoint> get() = trackingState.pathPoints
}