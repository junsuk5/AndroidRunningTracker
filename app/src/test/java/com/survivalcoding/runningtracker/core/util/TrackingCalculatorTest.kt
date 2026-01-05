package com.survivalcoding.runningtracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TrackingCalculatorTest {

    @Test
    fun calculateDistance_returnsCorrectValue() {
        // Seoul City Hall to Gwanghwamun Gate (approx 600-700m)
        val lat1 = 37.5665
        val lon1 = 126.9780
        val lat2 = 37.5759
        val lon2 = 126.9768

        val distance = TrackingCalculator.calculateDistance(lat1, lon1, lat2, lon2)

        // Haversine calculation results in approx 1048 meters for these two points
        // Let's use a more precise expected value based on standard tools
        assertEquals(1048.0, distance, 10.0) // 10m tolerance
    }

    @Test
    fun calculateAvgSpeed_returnsCorrectValue() {
        // 10km in 1 hour = 10 km/h
        assertEquals(10f, TrackingCalculator.calculateAvgSpeed(10000, 3600000), 0.1f)

        // 5km in 30 minutes = 10 km/h
        assertEquals(10f, TrackingCalculator.calculateAvgSpeed(5000, 1800000), 0.1f)

        // 0 distance
        assertEquals(0f, TrackingCalculator.calculateAvgSpeed(0, 3600000), 0.1f)

        // 0 time
        assertEquals(0f, TrackingCalculator.calculateAvgSpeed(10000, 0), 0.1f)
    }
}
