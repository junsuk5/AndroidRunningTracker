package com.survivalcoding.runningtracker.presentation.service

import com.survivalcoding.runningtracker.domain.model.LocationPoint

data class TrackingState(
    val isTracking: Boolean = false,
    val pathPoints: List<LocationPoint> = emptyList(),
    val distanceInMeters: Int = 0,
    val timeInMillis: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val caloriesBurned: Int = 0
)
