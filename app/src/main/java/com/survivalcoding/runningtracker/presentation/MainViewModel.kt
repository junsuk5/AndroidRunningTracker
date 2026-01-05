package com.survivalcoding.runningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.use_case.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val saveRunUseCase: SaveRunUseCase,
    private val getRunsSortedByDateUseCase: GetRunsSortedByDateUseCase,
    private val getRunsSortedByDistanceUseCase: GetRunsSortedByDistanceUseCase,
    private val getRunsSortedByTimeInMillisUseCase: GetRunsSortedByTimeInMillisUseCase,
    private val getRunsSortedByAvgSpeedUseCase: GetRunsSortedByAvgSpeedUseCase,
    private val getRunsSortedByCaloriesBurnedUseCase: GetRunsSortedByCaloriesBurnedUseCase,
    private val getTotalStatsUseCase: GetTotalStatsUseCase,
    private val deleteRunUseCase: DeleteRunUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private var getRunsJob: Job? = null
    private var currentSortType = SortType.DATE

    init {
        getRuns(currentSortType)
        getTotalStats()
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.ToggleRun -> {
                _state.update { it.copy(isTracking = !it.isTracking) }
            }
            is MainAction.FinishRun -> {
                val currentState = _state.value
                val run = Run(
                    distanceInMeters = currentState.currentDistanceInMeters,
                    timeInMillis = currentState.currentTimeInMillis,
                    timestamp = System.currentTimeMillis(),
                    avgSpeedInKMH = currentState.currentAvgSpeedInKMH,
                    caloriesBurned = currentState.currentCaloriesBurned
                )
                viewModelScope.launch {
                    saveRunUseCase(run)
                }
                _state.update { it.copy(isTracking = false) }
            }
            is MainAction.DeleteRun -> {
                viewModelScope.launch {
                    deleteRunUseCase(action.run)
                }
            }
            is MainAction.ChangeSortType -> {
                if (currentSortType != action.sortType) {
                    currentSortType = action.sortType
                    getRuns(currentSortType)
                }
            }
        }
    }

    private fun getRuns(sortType: SortType) {
        getRunsJob?.cancel()
        val runsFlow = when (sortType) {
            SortType.DATE -> getRunsSortedByDateUseCase()
            SortType.DISTANCE -> getRunsSortedByDistanceUseCase()
            SortType.RUNNING_TIME -> getRunsSortedByTimeInMillisUseCase()
            SortType.AVG_SPEED -> getRunsSortedByAvgSpeedUseCase()
            SortType.CALORIES -> getRunsSortedByCaloriesBurnedUseCase()
        }

        getRunsJob = runsFlow.onEach { runs ->
            _state.update { it.copy(runs = runs) }
        }.launchIn(viewModelScope)
    }

    private fun getTotalStats() {
        combine(
            getTotalStatsUseCase.getTotalDistance(),
            getTotalStatsUseCase.getTotalTimeInMillis(),
            getTotalStatsUseCase.getTotalAvgSpeed(),
            getTotalStatsUseCase.getTotalCaloriesBurned()
        ) { distance, time, avgSpeed, calories ->
            _state.update {
                it.copy(
                    currentDistanceInMeters = distance,
                    currentTimeInMillis = time,
                    currentAvgSpeedInKMH = avgSpeed,
                    currentCaloriesBurned = calories
                )
            }
        }.launchIn(viewModelScope)
    }
}