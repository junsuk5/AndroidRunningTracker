import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.survivalcoding.runningtracker.presentation.MainAction
import com.survivalcoding.runningtracker.presentation.MainEvent
import com.survivalcoding.runningtracker.presentation.MainScreen
import com.survivalcoding.runningtracker.presentation.MainViewModel
import com.survivalcoding.runningtracker.presentation.designsystem.AppTheme
import com.survivalcoding.runningtracker.presentation.service.TrackingService
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainRoot(
    viewModel: MainViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.values.all { it }
        if (allGranted) {
            // 권한 승인 시 서비스 시작
            val intent = Intent(context, TrackingService::class.java).apply {
                action = TrackingService.ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } else {
            // 권한 거부 시 경고
            viewModel.onAction(MainAction.ToggleRun) // 트래킹 상태 원복
            // TODO: Snackbar 등으로 안내
        }
    }

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
                MainEvent.StartService -> {
                    val hasAllPermissions = permissions.all {
                        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    }

                    if (hasAllPermissions) {
                        val intent = Intent(context, TrackingService::class.java).apply {
                            action = TrackingService.ACTION_START
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent)
                        } else {
                            context.startService(intent)
                        }
                    } else {
                        permissionLauncher.launch(permissions.toTypedArray())
                    }
                }
                MainEvent.StopService -> {
                    val intent = Intent(context, TrackingService::class.java).apply {
                        action = TrackingService.ACTION_STOP
                    }
                    context.startService(intent)
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
