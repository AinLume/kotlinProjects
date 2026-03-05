package com.example.prac14.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.prac14.presentation.viewmodel.CompassState
import com.example.prac14.presentation.viewmodel.CompassViewModel

@Composable
fun CompassScreen(
    vm: CompassViewModel = viewModel()
) {
    val compassState by vm.compassState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> vm.registerSensors(context)
                Lifecycle.Event.ON_PAUSE  -> vm.unregisterSensors()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Компас",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (val state = compassState) {
            is CompassState.SensorNotAvailable -> {
                Text(
                    text = "Устройство не поддерживает датчик ориентации",
                    color = Color.Red,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }

            is CompassState.Active -> {
                CompassDial(azimuth = state.azimuth)

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Азимут: ${state.azimuth.toInt()}°",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = getDirectionName(state.azimuth),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getDirectionName(azimuth: Float): String = when {
    azimuth < 22.5 || azimuth >= 337.5 -> "Север"
    azimuth < 67.5 -> "Северо-восток"
    azimuth < 112.5 -> "Восток"
    azimuth < 157.5 -> "Юго-восток"
    azimuth < 202.5 -> "Юг"
    azimuth < 247.5 -> "Юго-запад"
    azimuth < 292.5 -> "Запад"
    else -> "Северо-запад"
}