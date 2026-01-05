package com.survivalcoding.runningtracker.data.location

import com.survivalcoding.runningtracker.domain.location.GpsStatusProvider
import com.survivalcoding.runningtracker.domain.model.GpsStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MockGpsStatusProvider : GpsStatusProvider {
    private val _status = MutableStateFlow<GpsStatus>(GpsStatus.Acquired)
    val status: StateFlow<GpsStatus> = _status.asStateFlow()

    override fun observeGpsStatus() = status

    fun toggleStatus() {
        _status.value = when (_status.value) {
            GpsStatus.Disabled -> GpsStatus.Enabled
            GpsStatus.Enabled -> GpsStatus.Acquired
            GpsStatus.Acquired -> GpsStatus.Lost
            GpsStatus.Lost -> GpsStatus.Disabled
        }
    }
}
