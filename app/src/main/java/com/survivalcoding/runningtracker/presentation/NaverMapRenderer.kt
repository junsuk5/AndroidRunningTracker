package com.survivalcoding.runningtracker.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.LocationPoint

class NaverMapRenderer : MapRenderer {
    @Composable
    override fun DrawMap(pathPoints: List<LocationPoint>) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF03C75A).copy(alpha = 0.2f)), // Naver Green
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Naver Maps Ready\nPoints: ${pathPoints.size}",
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
