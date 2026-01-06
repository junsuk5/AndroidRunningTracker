package com.survivalcoding.runningtracker.presentation.service

import androidx.compose.runtime.Stable
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class TrackingState(
    val isTracking: Boolean = false,
    val pathPoints: ImmutableList<LocationPoint> = persistentListOf(),
    val distanceInMeters: Double = 0.0,
    val timeInMillis: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val caloriesBurned: Int = 0
)
