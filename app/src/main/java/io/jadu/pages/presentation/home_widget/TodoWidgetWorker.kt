package io.jadu.pages.presentation.home_widget

// TodoWidgetWorker.kt
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class TodoWidgetWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val todos = runBlocking {
            TodoDataProvider.getTodos(applicationContext)
        }

        todos.firstOrNull()?.let {
            TodoWidget.updateTodos(applicationContext)
        }

        return Result.success()
    }
}
