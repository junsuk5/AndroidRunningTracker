package com.survivalcoding.runningtracker.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: RunEntity)

    @Delete
    suspend fun deleteRun(run: RunEntity)

    @Query("SELECT * FROM runs ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): Flow<List<RunEntity>>

    @Query("SELECT * FROM runs ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<RunEntity>>

    @Query("SELECT SUM(timeInMillis) FROM runs")
    fun getTotalTimeInMillis(): Flow<Long?>

    @Query("SELECT SUM(caloriesBurned) FROM runs")
    fun getTotalCaloriesBurned(): Flow<Int?>

    @Query("SELECT SUM(distanceInMeters) FROM runs")
    fun getTotalDistance(): Flow<Int?>

    @Query("SELECT AVG(avgSpeedInKMH) FROM runs")
    fun getTotalAvgSpeed(): Flow<Float?>
}
