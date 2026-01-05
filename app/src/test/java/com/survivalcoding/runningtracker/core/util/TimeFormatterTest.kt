package com.survivalcoding.runningtracker.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeFormatterTest {

    @Test
    fun formatTime_correctlyFormatsMilliseconds() {
        // 0ms
        assertEquals("00:00:00", TimeFormatter.formatTime(0))

        // 1 second
        assertEquals("00:00:01", TimeFormatter.formatTime(1000))

        // 1 minute
        assertEquals("00:01:00", TimeFormatter.formatTime(60000))

        // 1 hour
        assertEquals("01:00:00", TimeFormatter.formatTime(3600000))

        // Complex time: 1h 23m 45s
        val ms = (1 * 3600 + 23 * 60 + 45) * 1000L
        assertEquals("01:23:45", TimeFormatter.formatTime(ms))
    }
}
