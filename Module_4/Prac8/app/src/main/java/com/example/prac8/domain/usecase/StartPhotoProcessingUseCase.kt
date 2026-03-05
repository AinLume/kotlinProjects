package com.example.prac8.domain.usecase

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.prac8.data.worker.CompressWorker
import com.example.prac8.data.worker.UploadWorker
import com.example.prac8.data.worker.WatermarkWorker
import com.example.prac8.di.WorkerConstants

class StartPhotoProcessingUseCase(
    private val workManager: WorkManager
) {
    fun execute(fileName: String) {

        val compressRequest = OneTimeWorkRequestBuilder<CompressWorker>()
            .setInputData(workDataOf(WorkerConstants.KEY_FILE_NAME to fileName))
            .addTag(WorkerConstants.TAG_COMPRESS)
            .build()

        val watermarkRequest = OneTimeWorkRequestBuilder<WatermarkWorker>()
            .addTag(WorkerConstants.TAG_WATERMARK)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .addTag(WorkerConstants.TAG_UPLOAD)
            .build()


        workManager
            .beginUniqueWork(
                WorkerConstants.CHAIN_NAME,
                ExistingWorkPolicy.REPLACE,
                compressRequest
            )
            .then(watermarkRequest)
            .then(uploadRequest)
            .enqueue()
    }
}