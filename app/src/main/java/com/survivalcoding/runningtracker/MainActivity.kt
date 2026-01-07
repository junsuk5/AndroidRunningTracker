package com.survivalcoding.runningtracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.survivalcoding.runningtracker.presentation.MainRoot
import com.survivalcoding.runningtracker.presentation.designsystem.RunningTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 어두운 테마라고 알리기 --> 알림바 아이콘 밝은색으로
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                Color.TRANSPARENT
            )
        )
        setContent {
            RunningTrackerTheme {
                MainRoot()
            }
        }
    }
}