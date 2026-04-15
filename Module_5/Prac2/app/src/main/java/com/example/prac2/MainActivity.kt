package com.example.prac2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prac2.presentation.components.GalleryNavGraph
import com.example.prac2.ui.theme.Prac2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Prac2Theme {
                GalleryNavGraph()
            }
        }
    }
}