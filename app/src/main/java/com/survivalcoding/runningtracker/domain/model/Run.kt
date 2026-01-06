package com.survivalcoding.runningtracker.domain.model

import androidx.compose.runtime.Stable
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Stable
data class Run(
    val id: Int? = null,
    val distanceInMeters: Double = 0.0,
    val timeInMillis: Long = 0L,
    val timestamp: Long = 0L,
    val avgSpeedInKMH: Float = 0f,
    val caloriesBurned: Int = 0,
    val pathPoints: ImmutableList<LocationPoint> = persistentListOf()
)
