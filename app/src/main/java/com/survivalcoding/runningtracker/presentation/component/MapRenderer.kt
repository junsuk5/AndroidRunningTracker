package com.survivalcoding.runningtracker.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.survivalcoding.runningtracker.domain.model.LocationPoint

@Stable
interface MapRenderer {
    @Composable
    fun DrawMap(pathPoints: List<LocationPoint>)
}
