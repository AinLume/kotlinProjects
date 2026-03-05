package com.example.prac9.domain.usecase

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.prac9.data.worker.CityWeatherWorker
import com.example.prac9.data.worker.FinalReportWorker
import com.example.prac9.di.WorkerConstants
import androidx.core.content.edit

class StartWeatherFetchUseCase(
    private val workManager: WorkManager
) {

    private val cities = listOf("Москва", "Лондон", "Нью-Йорк", "Токио")

    fun execute(context: Context) {

        context.getSharedPreferences(
            WorkerConstants.PREFS_NAME,
            Context.MODE_PRIVATE
        ).edit { clear() }

        val cityRequests = cities.map { city ->
            OneTimeWorkRequestBuilder<CityWeatherWorker>()
                .setInputData(workDataOf(WorkerConstants.KEY_CITY_NAME to city))
                .addTag(WorkerConstants.TAG_CITY)
                .addTag(city)
                .build()
        }

        val finalRequest = OneTimeWorkRequestBuilder<FinalReportWorker>()
            .setInputData(
                workDataOf(
                    WorkerConstants.KEY_CITY_NAME to cities.toTypedArray(),
                    WorkerConstants.KEY_TEMPERATURE to intArrayOf(0, 0, 0, 0)
                )
            )
            .addTag(WorkerConstants.TAG_FINAL)
            .build()

        workManager
            .beginUniqueWork(
                WorkerConstants.CHAIN_NAME,
                ExistingWorkPolicy.REPLACE,
                cityRequests
            )
            .then(finalRequest)
            .enqueue()
    }
}