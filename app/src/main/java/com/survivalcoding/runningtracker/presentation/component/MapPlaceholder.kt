package com.survivalcoding.runningtracker.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme

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

@Preview(showBackground = true)
@Composable
fun MapPlaceholderPreview() {
    RunningTrackerTheme {
        MapPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
