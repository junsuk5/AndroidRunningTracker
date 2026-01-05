package com.survivalcoding.runningtracker.data.location

import com.survivalcoding.runningtracker.domain.location.LocationClient
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class MockLocationClient : LocationClient {
    override fun getLocationUpdates(interval: Long): Flow<LocationPoint> = flow {
        var lat = 37.5665
        var lng = 126.9780
        while (true) {
            emit(LocationPoint(lat, lng))
            delay(interval)
            // 약간의 랜덤 이동 시뮬레이션
            lat += (Random.nextDouble() - 0.5) * 0.0001
            lng += (Random.nextDouble() - 0.5) * 0.0001
        }
    }
}
