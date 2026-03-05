package com.example.prac8.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.prac8.di.WorkerConstants
import kotlinx.coroutines.delay

class CompressWorker (
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {

            val fileName = inputData.getString(WorkerConstants.KEY_FILE_NAME)
                ?: return Result.failure()


            for (i in 0..100 step 10) {
                delay(300)
                setProgress(workDataOf(WorkerConstants.KEY_PROGRESS to i))
            }

            val output = workDataOf(WorkerConstants.KEY_FILE_NAME to "compressed_$fileName")
            Result.success(output)

        } catch (e: Exception) {
            Result.failure()
        }
    }
}