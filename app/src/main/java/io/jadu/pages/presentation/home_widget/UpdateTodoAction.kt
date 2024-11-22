package io.jadu.pages.presentation.home_widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import io.jadu.pages.MainActivity
import io.jadu.pages.TodoActivity
import kotlinx.coroutines.flow.firstOrNull

class UpdateTodoAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val todoId = parameters[ActionParameters.Key<Long>("todoId")]
        val activityId = parameters[ActionParameters.Key<String>("activityId")]
        if(activityId!=null){
            context.startActivity(Intent(context, TodoActivity::class.java).apply {
                putExtra("addTodo", activityId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
        }

        if (todoId != null) {
            TodoDataProvider.updateTodo(context, todoId, true)
            WorkManager.getInstance(context).enqueue(
                OneTimeWorkRequestBuilder<TodoWidgetWorker>().build()
            )
            //TodoWidget.update(context, glanceId)
        }

    }
}
