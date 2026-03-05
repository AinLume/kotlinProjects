package com.example.prac13.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prac13.presentation.viewmodel.Direction

@Composable
fun DirectionIndicator(direction: Direction) {
    val (icon, color, label) = when (direction) {

        is Direction.UP -> Triple(
            Icons.Default.KeyboardArrowUp,
            Color.Green,
            "Рост")

        is Direction.DOWN -> Triple(
            Icons.Default.KeyboardArrowDown,
            Color.Red,
            "Снижение"
        )

        is Direction.NONE -> Triple(
            Icons.Default.Close,
            Color.Gray,
            "Без изменений"
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}