package com.survivalcoding.runningtracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survivalcoding.runningtracker.data.location.MockGpsStatusProvider
import com.survivalcoding.runningtracker.domain.location.GpsStatusProvider
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.domain.model.SortType
import com.survivalcoding.runningtracker.domain.use_case.DeleteRunUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByAvgSpeedUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByCaloriesBurnedUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByDateUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByDistanceUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetRunsSortedByTimeInMillisUseCase
import com.survivalcoding.runningtracker.domain.use_case.GetTotalStatsUseCase
import com.survivalcoding.runningtracker.domain.use_case.SaveRunUseCase
import com.survivalcoding.runningtracker.presentation.service.TrackingManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
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
    private val trackingManager: TrackingManager,
    private val gpsStatusProvider: GpsStatusProvider,
) : ViewModel() {

    private val _state = MutableStateFlow(MainState())
    val state: StateFlow<MainState> = _state.asStateFlow()

    private val _event = MutableSharedFlow<MainEvent>()
    val event: SharedFlow<MainEvent> = _event.asSharedFlow()

    private var getRunsJob: Job? = null
    private var gpsStatusJob: Job? = null

    init {
        getRuns(_state.value.sortType)
        getTotalStats()
        observeTrackingState()
        observeGpsStatus()

        // GPS Mocking 활성화 여부 설정
        _state.update {
            it.copy(isGpsMockingEnabled = gpsStatusProvider is MockGpsStatusProvider)
        }
    }

    private fun observeTrackingState() {
        trackingManager.state.onEach { trackingState ->
            _state.update {
                it.copy(
                    trackingState = trackingState,
                    displayPathPoints = if (trackingState.isTracking) trackingState.pathPoints else it.displayPathPoints
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeGpsStatus() {
        gpsStatusJob?.cancel()
        gpsStatusJob = gpsStatusProvider.observeGpsStatus().onEach { gpsStatus ->
            _state.update { it.copy(gpsStatus = gpsStatus) }
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
                _state.update {
                    it.copy(
                        selectedRun = action.run,
                        displayPathPoints = if (!it.isTracking) action.run.pathPoints else it.displayPathPoints
                    )
                }
            }

            MainAction.ToggleGpsStatus -> {
                (gpsStatusProvider as? MockGpsStatusProvider)?.toggleStatus()
            }
            MainAction.RefreshGpsStatus -> {
                observeGpsStatus()
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
            _state.update { it.copy(runs = runs.toImmutableList()) }
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