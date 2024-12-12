package io.jadu.pages.presentation.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jadu.pages.data.worker.NotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {

    private val _isNotificationBannerVisible = mutableStateOf(false)
    val isNotificationBannerVisible: State<Boolean> = _isNotificationBannerVisible

    fun scheduleDailyNotification(context: Context) {
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_notification_work",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    fun triggerNotificationNow(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    private fun calculateInitialDelay(): Long {
        val currentTime = Calendar.getInstance()
        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
        }
        val endTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
        }

        val randomTime = startTime.timeInMillis + (Math.random() * (endTime.timeInMillis - startTime.timeInMillis)).toLong()
        val delay = randomTime - currentTime.timeInMillis
        return if (delay > 0) delay else TimeUnit.DAYS.toMillis(1) + delay
    }

    fun checkNotificationPermission(context: Context) {
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        _isNotificationBannerVisible.value = !notificationManagerCompat.areNotificationsEnabled()
    }

    fun dismissNotificationBanner() {
        _isNotificationBannerVisible.value = false
    }


}