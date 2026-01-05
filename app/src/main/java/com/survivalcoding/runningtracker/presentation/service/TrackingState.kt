package com.survivalcoding.runningtracker.presentation.service

import androidx.compose.runtime.Stable
import com.survivalcoding.runningtracker.domain.model.LocationPoint

@Stable
data class TrackingState(
    val isTracking: Boolean = false,
    val pathPoints: List<LocationPoint> = emptyList(),
    val distanceInMeters: Double = 0.0,
    val timeInMillis: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val caloriesBurned: Int = 0
)
