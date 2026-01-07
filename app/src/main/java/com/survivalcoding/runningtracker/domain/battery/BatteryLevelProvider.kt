package com.survivalcoding.runningtracker.domain.battery

import kotlinx.coroutines.flow.Flow

interface BatteryLevelProvider {
    fun getBatteryLevel(): Flow<Int>
}
