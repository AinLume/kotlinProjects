package com.example.prac6.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.example.prac6.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun createMessage(context: Context, message: String) = NotificationCompat
        .Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Таймер")
        .setContentText(message)
        .build()

    fun sendNotification(context: Context, manager: NotificationManager, message: String) {
        manager.notify(NOTIFICATION_ID, createMessage(context, message))
    }
}

class TimerBackgroundService : Service() {
    private lateinit var manager: NotificationManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        manager = getSystemService<NotificationManager>()!!

        NotificationHelper.createNotificationChannel(this, manager)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seconds = intent?.getIntExtra("TIMER_SECONDS", 0) ?: 0

        CoroutineScope(Dispatchers.Default).launch {
            NotificationHelper.sendNotification(
                this@TimerBackgroundService,
                manager,
                "Таймер начал работу"
            )

            delay(seconds * 1000L)

            NotificationHelper.sendNotification(
                this@TimerBackgroundService,
                manager,
                "Таймер завершён! Прошло $seconds секунд"
            )
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}