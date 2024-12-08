package io.jadu.pages.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.jadu.pages.MainActivity
import io.jadu.pages.R
import io.jadu.pages.domain.usecase.GetNotificationUseCase

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val getNotificationUseCase: GetNotificationUseCase
) : Worker(context, params) {

    override fun doWork(): Result {
        createNotificationChannel()
        val content = getNotificationUseCase.execute()
        // Create an intent to launch MainActivity
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "daily_notification_channel")
            .setContentTitle("Daily Reminder")
            .setContentText(content)
            .setSmallIcon(R.drawable.baseline_book_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent) // Set the pending intent
            .setAutoCancel(true) // Remove the notification when tapped
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1001, notification)
        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Notification"
            val descriptionText = "Sends you sweet notifications everyday"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("daily_notification_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}