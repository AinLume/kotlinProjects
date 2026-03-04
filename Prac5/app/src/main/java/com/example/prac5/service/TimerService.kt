package com.example.prac5.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.prac5.R
import com.example.prac5.data.repository.TimerRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object NotificationHelper {
    private const val CHANNEL_ID = "my_timer_chanel"
    private const val CHANNEL_NAME = "Timer"
    const val NOTIFICATION_ID = 100

    fun createNotificationChannel(context: Context, manager: NotificationManager) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        manager.createNotificationChannel(channel)

    }

    fun createMessage(context: Context, seconds: Int) = NotificationCompat
        .Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Таймер")
        .setContentText("Прошло $seconds секунд")
        .build()

    fun sendNotification(context: Context, manager: NotificationManager, seconds: Int) {
        manager.notify(NOTIFICATION_ID, createMessage(context, seconds))
    }
}


class TimerService : Service() {
    private lateinit var manager: NotificationManager

    private var job: Job? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        manager = getSystemService<NotificationManager>()!!

        NotificationHelper.createNotificationChannel(this, manager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(
            NotificationHelper.NOTIFICATION_ID,
            NotificationHelper.createMessage(
                this,
                TimerRepositoryImpl.seconds.value
            )
        )

        job = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                delay(1000)

                TimerRepositoryImpl.updateSeconds(
                    TimerRepositoryImpl.seconds.value + 1
                )

                NotificationHelper.sendNotification(
                    this@TimerService,
                    manager,
                    TimerRepositoryImpl.seconds.value
                )
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()
        job = null
        TimerRepositoryImpl.reset()
    }
}