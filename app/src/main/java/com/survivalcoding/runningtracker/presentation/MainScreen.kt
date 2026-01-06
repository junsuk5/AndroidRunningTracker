package com.survivalcoding.runningtracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.presentation.component.GpsStatusBadge
import com.survivalcoding.runningtracker.presentation.component.MapRenderer
import com.survivalcoding.runningtracker.presentation.component.NopMapRenderer
import com.survivalcoding.runningtracker.presentation.component.RunItem
import com.survivalcoding.runningtracker.presentation.component.SortTypeSelector
import com.survivalcoding.runningtracker.presentation.component.TrackingOverlay
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme
import com.survivalcoding.runningtracker.presentation.service.TrackingState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppTheme.colors.surface)
            ) {
                mapRenderer.DrawMap(pathPoints = state.displayPathPoints)

                // GPS Status Badge
                GpsStatusBadge(
                    status = state.gpsStatus,
                    onClick = if (state.isGpsMockingEnabled) {
                        { onAction(MainAction.ToggleGpsStatus) }
                    } else null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(AppTheme.spacing.small)
                )
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
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = state.runs,
                    key = { it.id ?: it.timestamp }
                ) { run ->
                    RunItem(
                        run = run,
                        onDelete = { onAction(MainAction.DeleteRun(run)) },
                        onSelect = { onAction(MainAction.SelectRun(run)) }
                    )
                    Spacer(modifier = Modifier.height(AppTheme.spacing.small))
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    RunningTrackerTheme {
        MainScreen(
            state = MainState(
                runs = listOf(
                    Run(
                        id = 1,
                        distanceInMeters = 5000.0,
                        timeInMillis = 1800000,
                        timestamp = System.currentTimeMillis(),
                        avgSpeedInKMH = 10f,
                        caloriesBurned = 300,
                        pathPoints = persistentListOf()
                    ),
                    Run(
                        id = 2,
                        distanceInMeters = 3000.0,
                        timeInMillis = 1200000,
                        timestamp = System.currentTimeMillis(),
                        avgSpeedInKMH = 9f,
                        caloriesBurned = 200,
                        pathPoints = persistentListOf()
                    )
                ).toImmutableList()
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
                    distanceInMeters = 1200.0,
                    timeInMillis = 450000,
                    avgSpeedInKMH = 9.6f
                )
            ),
            onAction = {},
            mapRenderer = NopMapRenderer()
        )
    }
}
