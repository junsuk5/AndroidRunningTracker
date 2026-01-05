package com.survivalcoding.runningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.runningtracker.domain.model.Run
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
        trackingManager.isTracking.onEach { isTracking ->
            _state.update { it.copy(isTracking = isTracking) }
        }.launchIn(viewModelScope)

        trackingManager.distanceInMeters.onEach { distance ->
            if (_state.value.isTracking) {
                _state.update { it.copy(currentDistanceInMeters = distance) }
            }
        }.launchIn(viewModelScope)

        trackingManager.timeInMillis.onEach { time ->
            if (_state.value.isTracking) {
                _state.update { it.copy(currentTimeInMillis = time) }
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: MainAction) {
        when (action) {
            is MainAction.ToggleRun -> {
                val currentTrackingState = _state.value.isTracking
                viewModelScope.launch {
                    if (!currentTrackingState) {
                        _event.emit(MainEvent.StartService)
                    } else {
                        _event.emit(MainEvent.StopService)
                    }
                }
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
                    _event.emit(MainEvent.RunSaved)
                    _event.emit(MainEvent.StopService)
                }
            }
            is MainAction.DeleteRun -> {
                viewModelScope.launch {
                    deleteRunUseCase(action.run)
                    _event.emit(MainEvent.ShowSnackbar("운동 기록이 삭제되었습니다."))
                }
            }
            is MainAction.ChangeSortType -> {
                if (_state.value.sortType != action.sortType) {
                    _state.update { it.copy(sortType = action.sortType) }
                    getRuns(action.sortType)
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