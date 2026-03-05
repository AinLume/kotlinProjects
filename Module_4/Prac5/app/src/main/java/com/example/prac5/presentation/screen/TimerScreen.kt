package com.example.prac5.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prac5.presentation.viewmodel.TimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen (viewModel: TimerViewModel, startTimer: () -> Unit, stopTimer: () -> Unit) {

    val seconds by viewModel.seconds.collectAsState()

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Таймер оч крутой") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "$seconds секунд", fontSize = 48.sp )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = startTimer) {
                Text("Запустить таймер")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = stopTimer) {
                Text("Остановить таймер")
            }
        }
    }
}