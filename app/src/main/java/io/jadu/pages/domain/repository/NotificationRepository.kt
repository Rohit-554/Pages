package io.jadu.pages.domain.repository


interface NotificationRepository {
    fun getDailyNotificationContent(): String
}
