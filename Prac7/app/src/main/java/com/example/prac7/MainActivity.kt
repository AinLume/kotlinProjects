package com.example.prac7

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prac7.service.RandomNumberService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var service: RandomNumberService? = null
    var isBound by mutableStateOf(false)
        private set
    private var job: Job? = null
    var currentNumber by mutableIntStateOf(0)
        private set

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val randomBinder = binder as RandomNumberService.RandomBinder

            service = randomBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = currentNumber.toString(),
                    fontSize = 64.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(this@MainActivity, RandomNumberService::class.java)

                            bindService(intent, connection, Context.BIND_AUTO_CREATE)

                            job = CoroutineScope(Dispatchers.Main).launch {
                                while (true) {
                                    delay(1000)
                                    currentNumber = service?.currentNumber ?: 0
                                }
                            }
                        },
                        enabled = !isBound
                    ) {
                        Text("Подключиться")
                    }

                    Button(
                        onClick = {
                            if (isBound) {
                                unbindService(connection)
                                job?.cancel()
                                isBound = false
                                currentNumber = 0
                            }
                        },
                        enabled = isBound
                    ) {
                        Text("Отключиться")
                    }
                }
            }
        }
    }
}
