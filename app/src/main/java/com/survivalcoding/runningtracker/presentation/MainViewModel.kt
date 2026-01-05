package com.survivalcoding.runningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.model.SortType
import com.survivalcoding.runningtracker.domain.use_case.*
import com.survivalcoding.runningtracker.presentation.service.TrackingManager
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
    private val deleteRunUseCase: DeleteRunUseCase,
    private val trackingManager: TrackingManager
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _event.asSharedFlow()

    private var getRunsJob: Job? = null

    init {
        getRuns(_state.value.sortType)
        getTotalStats()
        observeTrackingState()
    }

    private fun observeTrackingState() {
        trackingManager.state.onEach { trackingState ->
            _state.update { 
                it.copy(trackingState = trackingState)
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.ToggleRun -> {
                val isTracking = _state.value.trackingState.isTracking
                viewModelScope.launch {
                    if (!isTracking) {
                        _state.update { it.copy(selectedRun = null) }
                        _event.emit(MainEvent.StartService)
                    } else {
                        _event.emit(MainEvent.StopService)
                    }
                }
            }
            is MainAction.FinishRun -> {
                val trackingState = _state.value.trackingState
                val run = Run(
                    distanceInMeters = trackingState.distanceInMeters,
                    timeInMillis = trackingState.timeInMillis,
                    timestamp = System.currentTimeMillis(),
                    avgSpeedInKMH = trackingState.avgSpeedInKMH,
                    caloriesBurned = trackingState.caloriesBurned,
                    pathPoints = trackingState.pathPoints
                )
                viewModelScope.launch {
                    _event.emit(MainEvent.StopService)
                    saveRunUseCase(run)
                    _event.emit(MainEvent.RunSaved)
                }
            }
            is MainAction.DeleteRun -> {
                viewModelScope.launch {
                    deleteRunUseCase(action.run)
                    if (_state.value.selectedRun?.id == action.run.id) {
                        _state.update { it.copy(selectedRun = null) }
                    }
                }
            }
            is MainAction.ChangeSortType -> {
                if (_state.value.sortType != action.sortType) {
                    _state.update { it.copy(sortType = action.sortType) }
                    getRuns(action.sortType)
                }
            }
            is MainAction.SelectRun -> {
                _state.update { it.copy(selectedRun = action.run) }
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
            SortType.CALORIES_BURNED -> getRunsSortedByCaloriesBurnedUseCase()
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
                    totalDistanceInMeters = distance,
                    totalTimeInMillis = time,
                    totalAvgSpeedInKMH = avgSpeed,
                    totalCaloriesBurned = calories
                )
            }
        }.launchIn(viewModelScope)
    }
}