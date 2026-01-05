package com.survivalcoding.runningtracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.core.util.TimeFormatter
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme
import com.survivalcoding.runningtracker.presentation.service.TrackingState

@Composable
fun TrackingOverlay(
    trackingState: TrackingState,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.normal),
        shape = RoundedCornerShape(24.dp),
        color = AppTheme.colors.primary,
        contentColor = AppTheme.colors.onPrimary,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .padding(AppTheme.spacing.normal)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TrackingInfoItem(
                    label = "Distance",
                    value = String.format("%.2f km", trackingState.distanceInMeters / 1000.0)
                )
                VerticalDivider(
                    modifier = Modifier.height(32.dp),
                    color = AppTheme.colors.onPrimary.copy(alpha = 0.2f)
                )
                TrackingInfoItem(
                    label = "Time",
                    value = TimeFormatter.formatTime(trackingState.timeInMillis)
                )
            }

            // Finish Button integrated into Overlay
            IconButton(
                onClick = onFinish,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppTheme.colors.onPrimary.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Finish Run",
                    tint = AppTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun TrackingInfoItem(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = AppTheme.typography.caption,
            color = AppTheme.colors.onPrimary.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            style = AppTheme.typography.h3,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun TrackingOverlayPreview() {
    RunningTrackerTheme {
        TrackingOverlay(
            trackingState = TrackingState(
                isTracking = true,
                distanceInMeters = 1250.0,
                timeInMillis = 480000,
                avgSpeedInKMH = 9.4f
            ),
            onFinish = {}
        )
    }
}
