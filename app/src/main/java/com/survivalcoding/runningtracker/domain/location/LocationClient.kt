package com.survivalcoding.runningtracker.domain.location

import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<LocationPoint>

    class LocationException(message: String) : Exception(message)
}
