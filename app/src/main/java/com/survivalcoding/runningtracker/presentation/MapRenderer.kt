package com.survivalcoding.runningtracker.presentation

import androidx.compose.runtime.Composable
import com.survivalcoding.runningtracker.domain.model.LocationPoint

interface MapRenderer {
    @Composable
    fun DrawMap(pathPoints: List<LocationPoint>)
}
