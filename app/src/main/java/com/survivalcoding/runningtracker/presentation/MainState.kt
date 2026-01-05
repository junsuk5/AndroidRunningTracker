package com.survivalcoding.runningtracker.presentation

import com.survivalcoding.runningtracker.domain.model.Run

data class MainState(
    val runs: List<Run> = emptyList(),
    val isTracking: Boolean = false,
    val currentDistanceInMeters: Int = 0,
    val currentTimeInMillis: Long = 0L,
    val currentAvgSpeedInKMH: Float = 0f,
    val currentCaloriesBurned: Int = 0,
    val pathPoints: List<List<Pair<Double, Double>>> = emptyList() // List of Polylines
)