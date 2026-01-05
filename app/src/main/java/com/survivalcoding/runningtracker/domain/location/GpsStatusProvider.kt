package com.survivalcoding.runningtracker.domain.location

import com.survivalcoding.runningtracker.domain.model.GpsStatus
import kotlinx.coroutines.flow.Flow

interface GpsStatusProvider {
    /**
     * GPS 상태 변화를 관찰할 수 있는 Flow를 반환합니다.
     */
    fun observeGpsStatus(): Flow<GpsStatus>
}
