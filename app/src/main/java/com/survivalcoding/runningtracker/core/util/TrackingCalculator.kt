package com.survivalcoding.runningtracker.core.util

import kotlin.math.*

object TrackingCalculator {
    /**
     * Calculates the distance between two coordinates in meters using the Haversine formula.
     */
    fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    /**
     * Calculates the average speed in km/h.
     */
    fun calculateAvgSpeed(distanceInMeters: Double, timeInMillis: Long): Float {
        val timeInSeconds = timeInMillis / 1000f
        return if (timeInSeconds > 0) {
            ((distanceInMeters / timeInSeconds) * 3.6).toFloat()
        } else {
            0f
        }
    }
}
