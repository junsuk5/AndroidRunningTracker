package com.survivalcoding.runningtracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.LocationPoint
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.presentation.component.MapRenderer
import com.survivalcoding.runningtracker.presentation.component.NopMapRenderer
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme
import com.survivalcoding.runningtracker.presentation.service.TrackingState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.concurrent.TimeUnit

@Composable
fun MainScreen(
    state: MainState,
    onAction: (MainAction) -> Unit,
    mapRenderer: MapRenderer,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.spacing.normal)
        ) {
            Text(
                text = "Running Tracker",
                style = AppTheme.typography.h1,
                color = AppTheme.colors.primary
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.normal))

            // Map Area (Renderer)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surface)
            ) {
                mapRenderer.DrawMap(pathPoints = state.trackingState.pathPoints.toImmutableList())
            }

            Spacer(modifier = Modifier.height(AppTheme.spacing.normal))

            // Sort Type Selector
            SortTypeSelector(
                selectedSortType = state.sortType,
                onSortTypeChange = { onAction(MainAction.ChangeSortType(it)) }
            )

            Spacer(modifier = Modifier.height(AppTheme.spacing.small))

            // Run List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
            ) {
                items(state.runs) { run ->
                    RunItem(
                        run = run,
                        onDelete = { onAction(MainAction.DeleteRun(run)) }
                    )
                }
            }
        }

        // Tracking Overlay
        AnimatedVisibility(
            visible = state.isTracking,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            TrackingOverlay(
                trackingState = state.trackingState,
                onFinish = { onAction(MainAction.FinishRun) }
            )
        }
    }
}

@Composable
fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(AppTheme.colors.surface)
            .padding(AppTheme.spacing.normal),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Map,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.1f),
            tint = AppTheme.colors.primary
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "GPS Map Placeholder",
                style = AppTheme.typography.body1,
                color = AppTheme.colors.secondaryText
            )
            Text(
                text = "Ready to Track",
                style = AppTheme.typography.caption,
                color = AppTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SortTypeSelector(
    selectedSortType: SortType,
    onSortTypeChange: (SortType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.small)
    ) {
        SortType.entries.forEach { sortType ->
            val isSelected = sortType == selectedSortType
            Surface(
                onClick = { onSortTypeChange(sortType) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface,
                contentColor = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onSurface,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Text(
                    text = sortType.name.replace("_", " "),
                    style = AppTheme.typography.caption,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RunItem(
    run: Run,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface,
            contentColor = AppTheme.colors.onSurface
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(AppTheme.spacing.normal)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                RunInfoRow(
                    icon = Icons.Default.History,
                    label = "Distance",
                    value = "${run.distanceInMeters / 1000f} km"
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.tiny))
                RunInfoRow(
                    icon = Icons.Default.Timer,
                    label = "Time",
                    value = formatTime(run.timeInMillis)
                )
                Spacer(modifier = Modifier.height(AppTheme.spacing.tiny))
                RunInfoRow(
                    icon = Icons.Default.Speed,
                    label = "Avg Speed",
                    value = "${run.avgSpeedInKMH} km/h"
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AppTheme.colors.error.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = AppTheme.colors.error
                )
            }
        }
    }
}

@Composable
fun RunInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = AppTheme.colors.primary
        )
        Spacer(modifier = Modifier.width(AppTheme.spacing.small))
        Text(
            text = "$label: ",
            style = AppTheme.typography.caption,
            color = AppTheme.colors.secondaryText
        )
        Text(
            text = value,
            style = AppTheme.typography.body2,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TrackingOverlay(
    trackingState: TrackingState,
    onFinish: () -> Unit,
) {
    Surface(
        modifier = Modifier
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
                    value = "${trackingState.distanceInMeters / 1000f} km"
                )
                VerticalDivider(
                    modifier = Modifier.height(32.dp),
                    color = AppTheme.colors.onPrimary.copy(alpha = 0.2f)
                )
                TrackingInfoItem(
                    label = "Time",
                    value = formatTime(trackingState.timeInMillis)
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

fun formatTime(ms: Long): String {
    val hrs = TimeUnit.MILLISECONDS.toHours(ms)
    val mins = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val secs = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%02d:%02d:%02d", hrs, mins, secs)
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RunningTrackerTheme {
        MainScreen(
            state = MainState(
                runs = listOf(
                    Run(
                        id = 1,
                        distanceInMeters = 5000,
                        timeInMillis = 1800000,
                        timestamp = System.currentTimeMillis(),
                        avgSpeedInKMH = 10f,
                        caloriesBurned = 300
                    ),
                    Run(
                        id = 2,
                        distanceInMeters = 3000,
                        timeInMillis = 1200000,
                        timestamp = System.currentTimeMillis(),
                        avgSpeedInKMH = 9f,
                        caloriesBurned = 200
                    )
                )
            ),
            onAction = {},
            mapRenderer = NopMapRenderer()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenTrackingPreview() {
    RunningTrackerTheme {
        MainScreen(
            state = MainState(
                trackingState = TrackingState(
                    isTracking = true,
                    distanceInMeters = 1200,
                    timeInMillis = 450000,
                    avgSpeedInKMH = 9.6f
                )
            ),
            onAction = {},
            mapRenderer = NopMapRenderer()
        )
    }
}
