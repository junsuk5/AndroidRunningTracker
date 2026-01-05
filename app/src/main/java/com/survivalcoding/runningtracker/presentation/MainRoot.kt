package com.survivalcoding.runningtracker.presentation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainRoot(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(true) {
        viewModel.event.collect { event ->
            when (event) {
                is MainEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                MainEvent.RunSaved -> {
                    snackbarHostState.showSnackbar("운동 기록이 저장되었습니다.")
                }
                is MainEvent.PermissionRequired -> {
                    // TODO: 권한 요청 로직 추가
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = !state.isTracking,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = { viewModel.onAction(MainAction.ToggleRun) },
                    containerColor = AppTheme.colors.primary,
                    contentColor = AppTheme.colors.onPrimary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Run"
                    )
                }
            }
        },
        containerColor = AppTheme.colors.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            MainScreen(
                state = state,
                onAction = viewModel::onAction
            )
        }
    }
}
