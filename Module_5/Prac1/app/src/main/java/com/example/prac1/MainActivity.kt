package com.example.prac1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prac1.presentation.components.DiaryNavGraph
import com.example.prac1.ui.theme.Prac1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prac1Theme {
                DiaryNavGraph()
            }
        }
    }
}