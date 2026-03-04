package com.example.prac6

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import com.example.prac6.presentation.screen.TimerScreen
import com.example.prac6.service.TimerBackgroundService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)

        setContent {
            MaterialTheme {
                TimerScreen({ seconds -> onStartTimer(seconds) })
            }
        }
    }

    fun onStartTimer(seconds: Int) {
        val intent = Intent(this, TimerBackgroundService::class.java)

        intent.putExtra("TIMER_SECONDS", seconds)

        startService(intent)
    }
}