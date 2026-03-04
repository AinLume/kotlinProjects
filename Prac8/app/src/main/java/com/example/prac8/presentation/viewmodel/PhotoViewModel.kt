package com.example.prac8.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.prac8.di.WorkerConstants
import com.example.prac8.domain.usecase.StartPhotoProcessingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

class PhotoViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)
    private val startProcessingUseCase = StartPhotoProcessingUseCase(workManager)

    data class UiState(
        val statusText: String = "Готов к обработке",
        val progress: Float = 0f,
        val isRunning: Boolean = false,
        val resultText: String = "",
        val isError: Boolean = false
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        observeChain()
    }

    fun startProcessing() {
        _uiState.value = UiState(statusText = "Запускаем...", isRunning = true)
        startProcessingUseCase.execute("photo_${System.currentTimeMillis()}.jpg")
    }

    private fun observeChain() {
        workManager
            .getWorkInfosForUniqueWorkFlow(WorkerConstants.CHAIN_NAME)
            .onEach { infos ->
                val runningInfo = infos.firstOrNull { it.state == WorkInfo.State.RUNNING }
                val relevantInfo = runningInfo
                    ?: infos.lastOrNull { it.state == WorkInfo.State.SUCCEEDED }
                    ?: infos.firstOrNull { it.state == WorkInfo.State.FAILED }
                    ?: return@onEach

                handleWorkInfo(relevantInfo)
            }
            .launchIn(viewModelScope)
    }

    private fun handleWorkInfo(workInfo: WorkInfo) {
        val currentTag = when {
            workInfo.tags.contains(WorkerConstants.TAG_COMPRESS) -> WorkerConstants.TAG_COMPRESS
            workInfo.tags.contains(WorkerConstants.TAG_WATERMARK) -> WorkerConstants.TAG_WATERMARK
            workInfo.tags.contains(WorkerConstants.TAG_UPLOAD) -> WorkerConstants.TAG_UPLOAD
            else -> null
        }

        val statusText = when (currentTag) {
            WorkerConstants.TAG_COMPRESS -> "Сжимаем фото..."
            WorkerConstants.TAG_WATERMARK -> "Добавляем водяной знак..."
            WorkerConstants.TAG_UPLOAD -> "Загружаем в облако..."
            else -> "Обработка..."
        }

        val progress = workInfo.progress.getInt(WorkerConstants.KEY_PROGRESS, 0) / 100f

        when (workInfo.state) {
            WorkInfo.State.RUNNING -> {
                _uiState.value = UiState(
                    statusText = statusText,
                    progress = progress,
                    isRunning = true
                )
            }
            WorkInfo.State.SUCCEEDED -> {
                val resultFile = workInfo.outputData.getString(WorkerConstants.KEY_FILE_NAME)
                    ?: "файл загружен"
                _uiState.value = UiState(
                    statusText = "Готово",
                    progress = 1f,
                    isRunning = false,
                    resultText = "Фото загружено: $resultFile"
                )
            }
            WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                _uiState.value = UiState(
                    statusText = "Ошибка",
                    isRunning = false,
                    resultText = "Что-то пошло не так. Попробуйте снова.",
                    isError = true
                )
            }
            else -> { }
        }
    }
}