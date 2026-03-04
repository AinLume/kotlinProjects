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
import kotlin.random.Random
import androidx.core.content.edit

class CityWeatherWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val cityName = inputData.getString(WorkerConstants.KEY_CITY_NAME)
            ?: return Result.failure()

        setForeground(createForegroundInfo("Загружаем погоду для $cityName..."))

        delay(Random.nextLong(2000, 5000))
        val temperature = Random.nextInt(-10, 35)

        val prefs = applicationContext.getSharedPreferences(
            WorkerConstants.PREFS_NAME,
            Context.MODE_PRIVATE
        )
        prefs.edit {
            putInt(cityName, temperature)
        }

        return Result.success(
            workDataOf(
                WorkerConstants.KEY_CITY_NAME to cityName,
                WorkerConstants.KEY_TEMPERATURE to temperature
            )
        )
    }

    private fun createForegroundInfo(text: String): ForegroundInfo {
        val notification = buildNotification(text)
        return ForegroundInfo(
            WorkerConstants.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        )
    }

    private fun buildNotification(text: String): Notification {
        return NotificationCompat.Builder(applicationContext, WorkerConstants.CHANNEL_ID)
            .setContentTitle("Прогноз погоды")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .build()
    }
}