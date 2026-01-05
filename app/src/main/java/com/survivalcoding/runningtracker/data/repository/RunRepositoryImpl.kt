package com.survivalcoding.runningtracker.data.repository

import com.survivalcoding.runningtracker.data.database.RunDao
import com.survivalcoding.runningtracker.data.database.RunEntity
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RunRepositoryImpl(
    private val runDao: RunDao
) : RunRepository {
    override suspend fun insertRun(run: Run) {
        runDao.insertRun(RunEntity.fromRun(run))
    }

    override suspend fun deleteRun(run: Run) {
        runDao.deleteRun(RunEntity.fromRun(run))
    }

    override fun getAllRunsSortedByDate(): Flow<List<Run>> =
        runDao.getAllRunsSortedByDate().map { entities -> entities.map { it.toRun() } }

    override fun getAllRunsSortedByDistance(): Flow<List<Run>> =
        runDao.getAllRunsSortedByDistance().map { entities -> entities.map { it.toRun() } }

    override fun getAllRunsSortedByTimeInMillis(): Flow<List<Run>> =
        runDao.getAllRunsSortedByTimeInMillis().map { entities -> entities.map { it.toRun() } }

    override fun getAllRunsSortedByAvgSpeed(): Flow<List<Run>> =
        runDao.getAllRunsSortedByAvgSpeed().map { entities -> entities.map { it.toRun() } }

    override fun getAllRunsSortedByCaloriesBurned(): Flow<List<Run>> =
        runDao.getAllRunsSortedByCaloriesBurned().map { entities -> entities.map { it.toRun() } }

    override fun getTotalTimeInMillis(): Flow<Long> =
        runDao.getTotalTimeInMillis().map { it ?: 0L }

    override fun getTotalCaloriesBurned(): Flow<Int> =
        runDao.getTotalCaloriesBurned().map { it ?: 0 }

    override fun getTotalDistance(): Flow<Int> =
        runDao.getTotalDistance().map { it ?: 0 }

    override fun getTotalAvgSpeed(): Flow<Float> =
        runDao.getTotalAvgSpeed().map { it ?: 0f }
}
