package com.example.prac12

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prac12.presentation.screen.AnimalFactScreen
import com.example.prac12.ui.theme.Prac12Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prac12Theme {
                AnimalFactScreen()
            }
        }
    }
}