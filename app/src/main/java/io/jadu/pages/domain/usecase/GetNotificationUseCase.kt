package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.repository.NotificationRepository

// GetNotificationUseCase.kt
class GetNotificationUseCase(private val repository: NotificationRepository) {
    fun execute(): String {
        return repository.getDailyNotificationContent()
    }
}
