package com.survivalcoding.runningtracker.domain.model

import androidx.compose.runtime.Stable

@Stable
sealed interface GpsStatus {
    /**
     * GPS가 시스템 설정에서 꺼져 있는 상태
     */
    data object Disabled : GpsStatus

    /**
     * GPS가 켜져 있지만 아직 위치 신호를 찾고 있는 상태
     */
    data object Enabled : GpsStatus

    /**
     * 유효한 위치 데이터를 수신 중인 상태
     */
    data object Acquired : GpsStatus

    /**
     * GPS는 켜져 있으나 일시적으로 신호를 잃은 상태
     */
    data object Lost : GpsStatus
}
