package com.example.prac9.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.prac9.di.WorkerConstants
import com.example.prac9.domain.usecase.StartWeatherFetchUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)
    private val startUseCase = StartWeatherFetchUseCase(workManager)
    val cities = listOf("Москва", "Лондон", "Нью-Йорк", "Токио")

    data class CityCardState(
        val name: String,
        val isLoading: Boolean = false,
        val temperature: Int? = null
    )

    data class UiState(
        val cards: List<CityCardState> = emptyList(),
        val isRunning: Boolean = false,
        val report: String = ""
    )

    private val _uiState = MutableStateFlow(UiState(
        cards = listOf("Москва", "Лондон", "Нью-Йорк", "Токио").map { CityCardState(name = it) }
    ))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeWorkers()
    }

    fun startFetching() {
        _uiState.value = UiState(
            cards = cities.map { CityCardState(name = it, isLoading = true) },
            isRunning = true
        )
        startUseCase.execute(getApplication())
    }

    private fun observeWorkers() {
        workManager
            .getWorkInfosForUniqueWorkFlow(WorkerConstants.CHAIN_NAME)
            .onEach { infos -> processInfos(infos) }
            .launchIn(viewModelScope)
    }

    private fun processInfos(infos: List<WorkInfo>) {
        if (infos.isEmpty()) return

        val cityInfos = infos.filter { it.tags.contains(WorkerConstants.TAG_CITY) }
        val finalInfo = infos.firstOrNull { it.tags.contains(WorkerConstants.TAG_FINAL) }

        val updatedCards = cities.map { city ->
            val cityWorkInfo = cityInfos.firstOrNull { it.tags.contains(city) }

            when (cityWorkInfo?.state) {
                WorkInfo.State.SUCCEEDED -> {
                    // Читаем температуру из outputData завершённого Worker-а
                    val temp = cityWorkInfo.outputData.getInt(WorkerConstants.KEY_TEMPERATURE, 0)
                    CityCardState(name = city, isLoading = false, temperature = temp)
                }
                WorkInfo.State.RUNNING -> CityCardState(name = city, isLoading = true)
                WorkInfo.State.FAILED -> CityCardState(name = city, isLoading = false)
                else -> _uiState.value.cards.firstOrNull { it.name == city }
                    ?: CityCardState(name = city) // сохраняем текущее состояние если нет обновлений
            }
        }

        val report = if (finalInfo?.state == WorkInfo.State.SUCCEEDED) {
            finalInfo.outputData.getString(WorkerConstants.KEY_REPORT) ?: ""
        } else ""

        val isRunning = infos.any {
            it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
        }

        _uiState.value = UiState(
            cards = updatedCards,
            isRunning = isRunning,
            report = report
        )
    }
}