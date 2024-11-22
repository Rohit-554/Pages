package io.jadu.pages.presentation.home_widget

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import io.jadu.pages.domain.model.TodoModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

object TodoDataProvider {
    fun getTodos(context: Context): Flow<List<TodoModel>> {
        val todoUseCase = getTodoRepository(context)

        return runBlocking {
            todoUseCase.getAllTodos()
        }
    }

    fun updateTodo(context: Context, todoId: Long, isCompleted: Boolean) {
        val todoRepository = getTodoRepository(context)
        runBlocking {
            val todos = todoRepository.getAllTodos()
            todos.firstOrNull()?.forEach {
                Log.d("TodoDataProvider", "Todo Id: ${it.id}")
                if(it.id == todoId){
                    val updatedTodo = it.copy(isTaskCompleted = isCompleted)
                    todoRepository.updateTodo(updatedTodo)
                }
            }

            /*val todos = todoUseCase.invoke().first()*/
            /*todos.find { it.id == todoId }?.let { todo ->
                val updatedTodo = todo.copy(isTaskCompleted = isCompleted)
                todoUseCase.updateTodo(updatedTodo)
            }*/
        }
    }
}

class TodoDataStore(private val context:Context):DataStore<List<TodoModel>>{
    override val data: Flow<List<TodoModel>>
        get() = TodoDataProvider.getTodos(context)

    override suspend fun updateData(transform: suspend (t: List<TodoModel>) -> List<TodoModel>): List<TodoModel> {
        throw NotImplementedError("Not implemented in Todo Data Store")
    }

}

