package com.survivalcoding.runningtracker.core.util

import java.util.concurrent.TimeUnit

object TimeFormatter {
    fun formatTime(ms: Long): String {
        val hrs = TimeUnit.MILLISECONDS.toHours(ms)
        val mins = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
        val secs = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d:%02d", hrs, mins, secs)
    }
}
