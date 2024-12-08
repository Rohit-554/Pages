package io.jadu.pages.data.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import io.jadu.pages.domain.usecase.GetNotificationUseCase

class CustomWorkerFactory(
    private val getNotificationUseCase: GetNotificationUseCase
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (Class.forName(workerClassName)) {
            NotificationWorker::class.java -> NotificationWorker(appContext, workerParameters, getNotificationUseCase)
            else -> null
        }
    }
}
