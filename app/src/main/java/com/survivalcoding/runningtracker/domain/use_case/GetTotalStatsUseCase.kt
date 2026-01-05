package com.survivalcoding.runningtracker.domain.use_case

import com.survivalcoding.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class GetTotalStatsUseCase(
    private val repository: RunRepository
) {
    fun getTotalTimeInMillis(): Flow<Long> = repository.getTotalTimeInMillis()
    fun getTotalCaloriesBurned(): Flow<Int> = repository.getTotalCaloriesBurned()
    fun getTotalDistance(): Flow<Double> = repository.getTotalDistance()
    fun getTotalAvgSpeed(): Flow<Float> = repository.getTotalAvgSpeed()
}
