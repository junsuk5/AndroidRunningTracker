package com.survivalcoding.runningtracker.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import kotlinx.collections.immutable.ImmutableList

class LogMapRenderer : MapRenderer {
    @Composable
    override fun DrawMap(pathPoints: ImmutableList<LocationPoint>) {
        // 로그 출력
        Log.d("LogMapRenderer", "Total path points: ${pathPoints.size}")
        if (pathPoints.isNotEmpty()) {
            val lastPoint = pathPoints.last()
            Log.d("LogMapRenderer", "Current Location: lat=${lastPoint.latitude}, lng=${lastPoint.longitude}")
        }

        // 화면에 로그 텍스트로 표시 (Mock 시각화)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "--- 지도 로그 (Mock) ---",
                    color = Color.White
                )
            }
            items(pathPoints.takeLast(5)) { point ->
                Text(
                    text = "위도: ${point.latitude}, 경도: ${point.longitude}",
                    color = Color.White
                )
            }
        }
    }
}
