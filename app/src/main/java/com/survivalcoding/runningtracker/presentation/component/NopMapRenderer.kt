package com.survivalcoding.runningtracker.presentation.component

import androidx.compose.runtime.Composable
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.collections.immutable.ImmutableList

class NopMapRenderer : MapRenderer {
    @Composable
    override fun DrawMap(pathPoints: ImmutableList<LocationPoint>) {
        // 지도 표시 안 함 (Placeholder)
    }
}
