package com.survivalcoding.runningtracker.presentation

import androidx.compose.runtime.Composable
import com.survivalcoding.runningtracker.domain.model.LocationPoint

class NopMapRenderer : MapRenderer {
    @Composable
    override fun DrawMap(pathPoints: List<LocationPoint>) {
        // 지도 표시 안 함 (Placeholder)
    }
}
