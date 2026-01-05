package com.survivalcoding.runningtracker.presentation.service

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackingManager {
    private val _isTracking = MutableStateFlow(false)
    val isTracking: StateFlow<Boolean> = _isTracking.asStateFlow()

    fun updateTrackingState(isTracking: Boolean) {
        _isTracking.value = isTracking
    }
}
