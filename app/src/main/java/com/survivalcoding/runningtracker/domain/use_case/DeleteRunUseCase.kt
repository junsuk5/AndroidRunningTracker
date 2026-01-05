package com.survivalcoding.runningtracker.domain.use_case

import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.repository.RunRepository

class DeleteRunUseCase(
    private val repository: RunRepository
) {
    suspend operator fun invoke(run: Run) {
        repository.deleteRun(run)
    }
}
