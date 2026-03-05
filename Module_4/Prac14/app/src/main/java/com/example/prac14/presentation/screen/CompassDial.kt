package com.example.prac14.presentation.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompassDial(azimuth: Float) {

    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 300),
        label = "compass_rotation"
    )

    Box(contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier.size(280.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f

            drawCircle(
                color = Color(0xFF1A1A2E),
                radius = radius
            )

            drawCircle(
                color = Color(0xFF4A90D9),
                radius = radius,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
            )

            rotate(degrees = -animatedAzimuth, pivot = center) {

                val arrowLength = radius * 0.75f

                drawLine(
                    color = Color(0xFFE53935),
                    start = center,
                    end = Offset(center.x, center.y - arrowLength),
                    strokeWidth = 12.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                drawLine(
                    color = Color(0xFF757575),
                    start = center,
                    end = Offset(center.x, center.y + arrowLength),
                    strokeWidth = 12.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }

            drawCircle(
                color = Color.White,
                radius = 8.dp.toPx(),
                center = center
            )
        }

        Text(
            text = "N",
            color = Color(0xFFE53935),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )
    }
}