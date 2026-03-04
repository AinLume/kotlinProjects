package com.example.prac9.presentation.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun tempColor(temp: Int) = when {
    temp < 0  -> MaterialTheme.colorScheme.tertiary
    temp < 15 -> MaterialTheme.colorScheme.secondary
    else      -> MaterialTheme.colorScheme.error
}