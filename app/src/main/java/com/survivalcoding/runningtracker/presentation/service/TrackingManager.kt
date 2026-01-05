package com.survivalcoding.runningtracker.presentation.service

import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TrackingManager {
    private val _state = MutableStateFlow(TrackingState())
    val state: StateFlow<TrackingState> = _state.asStateFlow()

    fun updateTrackingState(isTracking: Boolean) {
        _state.update {
            if (!isTracking) {
                // 트래킹 종료 시 데이터 초기화
                TrackingState(isTracking = false)
            } else {
                it.copy(isTracking = true)
            }
        }
    }

    fun addPathPoint(point: LocationPoint) {
        _state.update {
            if (it.isTracking) {
                it.copy(pathPoints = it.pathPoints + point)
            } else it
        }
    }

    fun updateDistance(distance: Int) {
        _state.update {
            if (it.isTracking) {
                it.copy(distanceInMeters = distance)
            } else it
        }
    }

    fun updateTime(time: Long) {
        _state.update {
            if (it.isTracking) {
                it.copy(timeInMillis = time)
            } else it
        }
    }

    fun updateAvgSpeed(speed: Float) {
        _state.update {
            if (it.isTracking) {
                it.copy(avgSpeedInKMH = speed)
            } else it
        }
    }
}
