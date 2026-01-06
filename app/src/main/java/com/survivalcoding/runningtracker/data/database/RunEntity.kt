package com.survivalcoding.runningtracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.Run
import kotlinx.collections.immutable.toImmutableList

@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val distanceInMeters: Double,
    val timeInMillis: Long,
    val timestamp: Long,
    val avgSpeedInKMH: Float,
    val caloriesBurned: Int,
    val pathPoints: List<LocationPoint>
) {
    fun toRun(): Run = Run(
        id = id,
        distanceInMeters = distanceInMeters,
        timeInMillis = timeInMillis,
        timestamp = timestamp,
        avgSpeedInKMH = avgSpeedInKMH,
        caloriesBurned = caloriesBurned,
        pathPoints = pathPoints.toImmutableList()
    )

    companion object {
        fun fromRun(run: Run): RunEntity = RunEntity(
            id = run.id,
            distanceInMeters = run.distanceInMeters,
            timeInMillis = run.timeInMillis,
            timestamp = run.timestamp,
            avgSpeedInKMH = run.avgSpeedInKMH,
            caloriesBurned = run.caloriesBurned,
            pathPoints = run.pathPoints
        )
    }
}
