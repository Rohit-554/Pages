package io.jadu.pages.domain.repository

import io.jadu.pages.domain.model.TodoModel
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getAllTodos(): Flow<List<TodoModel>>
    suspend fun addTodo(todo: TodoModel)
    suspend fun updateTodo(todo: TodoModel)
    suspend fun deleteTodoById(todoId: Long)
}