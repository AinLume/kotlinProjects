package com.example.prac9.data.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.prac9.di.WorkerConstants
import kotlinx.coroutines.delay
import androidx.core.content.edit

class FinalReportWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo("Формируем отчёт..."))

        val prefs = applicationContext.getSharedPreferences(
            WorkerConstants.PREFS_NAME,
            Context.MODE_PRIVATE
        )

        val results = prefs.all
            .filterValues { it is Int }
            .mapValues { it.value as Int }

        if (results.isEmpty()) return Result.failure()

        delay(1500)

        val avgTemp = results.values.average().toInt()
        val cityList = results.keys.joinToString(", ")
        val report = "Города: $cityList | Средняя температура: $avgTemp°C"

        prefs.edit { clear() }

        updateFinalNotification("Отчёт готов! Средняя: $avgTemp°C")

        return Result.success(workDataOf(WorkerConstants.KEY_REPORT to report))
    }


    private fun createForegroundInfo(text: String): ForegroundInfo {
        val notification = buildNotification(text, true)
        return ForegroundInfo(
            WorkerConstants.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun updateFinalNotification(text: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager

        manager.notify(WorkerConstants.NOTIFICATION_ID, buildNotification(text, ongoing = false))
    }

    private fun buildNotification(text: String, ongoing: Boolean): Notification {
        return NotificationCompat.Builder(applicationContext, WorkerConstants.CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(ongoing)
            .build()
    }
}