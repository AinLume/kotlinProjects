package com.example.prac5

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.prac5.data.repository.TimerRepositoryImpl
import com.example.prac5.domain.usecase.GetSecondsUseCase
import com.example.prac5.presentation.screen.TimerScreen
import com.example.prac5.presentation.viewmodel.TimerViewModel
import com.example.prac5.service.TimerService

class MainActivity : ComponentActivity() {

    fun startTimer(intent: Intent) {
        startForegroundService(intent)
    }

    fun stopTimer(intent: Intent) {
        stopService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)

        val intent = Intent(this, TimerService::class.java)

        val timerRepository = TimerRepositoryImpl
        val getSecondsUseCase = GetSecondsUseCase(timerRepository)
        val viewModel = TimerViewModel(getSecondsUseCase)

        setContent {
            MaterialTheme {
                TimerScreen(
                    viewModel,
                    { startTimer(intent) },
                    { stopTimer(intent) }
                )
            }
        }
    }
}