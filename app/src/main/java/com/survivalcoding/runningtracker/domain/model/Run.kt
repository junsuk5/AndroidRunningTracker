package com.survivalcoding.runningtracker.domain.model

data class Run(
    val id: Int? = null,
    val distanceInMeters: Double = 0.0,
    val timeInMillis: Long = 0L,
    val timestamp: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val caloriesBurned: Int = 0,
    val pathPoints: List<LocationPoint> = emptyList()
)
