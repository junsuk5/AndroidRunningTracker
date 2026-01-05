package com.survivalcoding.runningtracker.presentation.service

import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrackingManager {
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    private val _pathPoints = MutableStateFlow<List<LocationPoint>>(emptyList())
    val pathPoints: StateFlow<List<LocationPoint>> = _pathPoints.asStateFlow()

    private val _distanceInMeters = MutableStateFlow(0)
    val distanceInMeters: StateFlow<Int> = _distanceInMeters.asStateFlow()

    private val _timeInMillis = MutableStateFlow(0L)
    val timeInMillis: StateFlow<Long> = _timeInMillis.asStateFlow()

    fun updateTrackingState(isTracking: Boolean) {
        _isTracking.value = isTracking
        if (!isTracking) {
            // 트래킹 종료 시 데이터 초기화 (필요에 따라)
            _pathPoints.value = emptyList()
            _distanceInMeters.value = 0
            _timeInMillis.value = 0L
        }
    }

    fun addPathPoint(point: LocationPoint) {
        _pathPoints.update { it + point }
    }

    fun updateDistance(distance: Int) {
        _distanceInMeters.value = distance
    }

    fun updateTime(time: Long) {
        _timeInMillis.value = time
    }
}
