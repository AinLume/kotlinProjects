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


object NotificationHelper {
    private const val CHANNEL_ID = "my_timer_chanel"
    private const val CHANNEL_NAME = "Timer"

    fun createNotificationChannel(context: Context) {

        val notificationManager: NotificationManager = context.getSystemService<NotificationManager>()!!

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        notificationManager.createNotificationChannel(channel)

    }

    fun createMessage(context: Context) = NotificationCompat.Builder(context,CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Title")
        .setContentText("Media player is starting!")
        .build()
}


class TimerService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)


    }

    override fun onDestroy() {
        super.onDestroy()
    }
}