package com.example.prac7.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class RandomNumberService : Service() {

    private var job: Job? = null

    var currentNumber: Int = 0
        private set

    inner class RandomBinder : Binder() {
        fun getService(): RandomNumberService = this@RandomNumberService
    }

    private val binder = RandomBinder()

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)
                currentNumber = Random.Default.nextInt(0, 101)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}