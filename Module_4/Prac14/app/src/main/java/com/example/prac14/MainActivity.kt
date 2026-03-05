package com.example.prac14

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prac14.presentation.screen.CompassScreen
import com.example.prac14.ui.theme.Prac14Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prac14Theme {
                CompassScreen()
            }
        }
    }
}