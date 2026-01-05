package com.survivalcoding.runningtracker.domain.use_case

import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class GetRunsSortedByDistanceUseCase(
    private val repository: RunRepository
) {
    operator fun invoke(): Flow<List<Run>> {
        return repository.getAllRunsSortedByDistance()
    }
}
