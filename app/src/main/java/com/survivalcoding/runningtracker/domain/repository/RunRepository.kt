package com.survivalcoding.runningtracker.domain.repository

import com.survivalcoding.runningtracker.domain.model.Run
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    suspend fun insertRun(run: Run)
    suspend fun deleteRun(run: Run)

    fun getAllRunsSortedByDate(): Flow<List<Run>>
    fun getAllRunsSortedByDistance(): Flow<List<Run>>
    fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>>
    fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>>
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>>

    fun getTotalTimeInMillis(): Flow<Long>
    fun getTotalCaloriesBurned(): Flow<Int>
    fun getTotalDistance(): Flow<Int>
    fun getTotalAvgSpeed(): Flow<Float>
}
