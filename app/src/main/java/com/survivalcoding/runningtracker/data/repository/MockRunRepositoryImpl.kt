package com.survivalcoding.runningtracker.data.repository

import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class MockRunRepositoryImpl : RunRepository {

    private val _runs = MutableStateFlow<List<Run>>(
        listOf(
            Run(1, 5000.0, 1500000L, System.currentTimeMillis() - 86400000L * 3, 12.0f, 400),
            Run(2, 3000.0, 900000L, System.currentTimeMillis() - 86400000L * 2, 12.0f, 250),
            Run(3, 8000.0, 2400000L, System.currentTimeMillis() - 86400000L * 1, 12.0f, 650),
            Run(4, 2000.0, 600000L, System.currentTimeMillis(), 12.0f, 150)
        )
    )

    override suspend fun insertRun(run: Run) {
        _runs.update { (it + run.copy(id = (it.maxByOrNull { r -> r.id ?: 0 }?.id ?: 0) + 1)) }
    }

    override suspend fun deleteRun(run: Run) {
        _runs.update { it.filter { r -> r.id != run.id } }
    }

    override fun getAllRunsSortedByDate(): Flow<List<Run>> =
        _runs.map { it.sortedByDescending { r -> r.timestamp } }

    override fun getAllRunsSortedByDistance(): Flow<List<Run>> =
        _runs.map { it.sortedByDescending { r -> r.distanceInMeters } }

    override fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>> =
        _runs.map { it.sortedByDescending { r -> r.timeInMillis } }

    override fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>> =
        _runs.map { it.sortedByDescending { r -> r.avgSpeedInKMH } }

    override fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> =
        _runs.map { it.sortedByDescending { r -> r.caloriesBurned } }

    override fun getTotalTimeInMillis(): Flow<Long> =
        _runs.map { it.sumOf { r -> r.timeInMillis } }

    override fun getTotalCaloriesBurned(): Flow<Int> =
        _runs.map { it.sumOf { r -> r.caloriesBurned } }

    override fun getTotalDistance(): Flow<Double> =
        _runs.map { it.sumOf { r -> r.distanceInMeters } }

    override fun getTotalAvgSpeed(): Flow<Float> =
        _runs.map {
            if (it.isEmpty()) 0f
            else it.map { r -> r.avgSpeedInKMH }.average().toFloat()
        }
}
