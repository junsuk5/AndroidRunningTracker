package com.survivalcoding.runningtracker.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.domain.model.Run
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme

@Composable
fun MainScreen(
    state: MainState,
    onAction: (MainAction) -> Unit
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
            
            // Map Placeholder
            MapPlaceholder(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
            )
            
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
            TrackingOverlay(state = state)
        }
        
        // FAB
        FloatingActionButton(
            onClick = { 
                if (state.isTracking) onAction(MainAction.FinishRun) else onAction(MainAction.ToggleRun)
            },
            containerColor = AppTheme.colors.primary,
            contentColor = AppTheme.colors.onPrimary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(AppTheme.spacing.normal)
        ) {
            Icon(
                imageVector = if (state.isTracking) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (state.isTracking) "Finish Run" else "Start Run"
            )
        }
    }
}

@Composable
fun MapPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(AppTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "GPS Map Placeholder",
                style = AppTheme.typography.body1,
                color = AppTheme.colors.secondaryText
            )
            Text(
                text = "Ready to Track",
                style = AppTheme.typography.caption,
                color = AppTheme.colors.primary
            )
        }
    }
}

@Composable
fun SortTypeSelector(
    selectedSortType: SortType,
    onSortTypeChange: (SortType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.tiny)
    ) {
        SortType.entries.forEach { sortType ->
            val isSelected = sortType == selectedSortType
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AppTheme.colors.primary else AppTheme.colors.surface)
                    .clickable { onSortTypeChange(sortType) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = sortType.name,
                    style = AppTheme.typography.caption,
                    color = if (isSelected) AppTheme.colors.onPrimary else AppTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
fun RunItem(
    run: Run,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.surface,
            contentColor = AppTheme.colors.onSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(AppTheme.spacing.normal)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Distance: ${run.distanceInMeters / 1000f} km", style = AppTheme.typography.body1)
                Text(text = "Time: ${run.timeInMillis / 1000} s", style = AppTheme.typography.body2, color = AppTheme.colors.secondaryText)
                Text(text = "Speed: ${run.avgSpeedInKMH} km/h", style = AppTheme.typography.caption)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AppTheme.colors.error)
            }
        }
    }
}

@Composable
fun TrackingOverlay(state: MainState) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppTheme.spacing.normal)
            .clip(RoundedCornerShape(24.dp))
            .background(AppTheme.colors.primary.copy(alpha = 0.9f))
            .padding(AppTheme.spacing.normal)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Tracking...", style = AppTheme.typography.caption, color = AppTheme.colors.onPrimary)
                Text(
                    text = "${state.currentDistanceInMeters / 1000f} km",
                    style = AppTheme.typography.h2,
                    color = AppTheme.colors.onPrimary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${state.currentTimeInMillis / 1000}s",
                    style = AppTheme.typography.body1,
                    color = AppTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${state.currentAvgSpeedInKMH} km/h",
                    style = AppTheme.typography.body2,
                    color = AppTheme.colors.onPrimary
                )
            }
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
                    Run(id = 1, distanceInMeters = 5000, timeInMillis = 1800000, timestamp = System.currentTimeMillis(), avgSpeedInKMH = 10f, caloriesBurned = 300),
                    Run(id = 2, distanceInMeters = 3000, timeInMillis = 1200000, timestamp = System.currentTimeMillis(), avgSpeedInKMH = 9f, caloriesBurned = 200)
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenTrackingPreview() {
    RunningTrackerTheme {
        MainScreen(
            state = MainState(
                isTracking = true,
                currentDistanceInMeters = 1200,
                currentTimeInMillis = 450000,
                currentAvgSpeedInKMH = 9.6f
            ),
            onAction = {}
        )
    }
}
