package com.survivalcoding.runningtracker.presentation.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun RunningTrackerTheme(
    colors: RunningTrackerColors = DarkColorPalette,
    typography: RunningTrackerTypography = DefaultTypography,
    spacing: RunningTrackerSpacing = RunningTrackerSpacing(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalRunningTrackerColors provides colors,
        LocalRunningTrackerTypography provides typography,
        LocalRunningTrackerSpacing provides spacing
    ) {
        content()
    }
}

object AppTheme {
    val colors: RunningTrackerColors
        @Composable
        @ReadOnlyComposable
        get() = LocalRunningTrackerColors.current

    val typography: RunningTrackerTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalRunningTrackerTypography.current

    val spacing: RunningTrackerSpacing
        @Composable
        @ReadOnlyComposable
        get() = LocalRunningTrackerSpacing.current
}
