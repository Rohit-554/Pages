package io.jadu.pages

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import io.jadu.pages.data.repository.NotificationRepositoryImpl
import io.jadu.pages.data.worker.CustomWorkerFactory
import io.jadu.pages.domain.usecase.GetNotificationUseCase

@HiltAndroidApp
class NotesApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        val repository = NotificationRepositoryImpl()
        val getNotificationUseCase = GetNotificationUseCase(repository)

        val customWorkerFactory = CustomWorkerFactory(getNotificationUseCase)

        val config = Configuration.Builder()
            .setWorkerFactory(customWorkerFactory)
            .build()

        WorkManager.initialize(this, config)
    }
}