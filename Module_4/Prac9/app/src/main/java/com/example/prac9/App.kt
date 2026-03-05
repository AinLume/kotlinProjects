package com.example.prac9

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.prac9.di.WorkerConstants

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            WorkerConstants.CHANNEL_ID,
            "Прогноз погоды",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Уведомления о загрузке погоды"
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}