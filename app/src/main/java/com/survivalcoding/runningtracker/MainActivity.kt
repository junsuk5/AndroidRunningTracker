package com.survivalcoding.runningtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.survivalcoding.runningtracker.presentation.MainEvent
import com.survivalcoding.runningtracker.presentation.MainScreen
import com.survivalcoding.runningtracker.presentation.MainViewModel
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RunningTrackerTheme {
                val viewModel: MainViewModel = koinViewModel()
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppTheme.colors.background)
                ) {
                    MainScreen(
                        state = state,
                        onAction = viewModel::onAction
                    )
                    
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        style = AppTheme.typography.h2,
        color = AppTheme.colors.primary,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RunningTrackerTheme {
        Box(modifier = Modifier.background(AppTheme.colors.background)) {
            Greeting("Running Tracker Admin")
        }
    }
}