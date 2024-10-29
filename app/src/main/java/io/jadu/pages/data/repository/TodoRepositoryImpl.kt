package io.jadu.pages.data.repository

import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.data.dao.TodoDao
import io.jadu.pages.domain.model.TodoModel
import io.jadu.pages.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
):TodoRepository {
    override fun getAllTodos(): Flow<List<TodoModel>> {
        return todoDao.getAllTodos()
    }

    override suspend fun addTodo(todo: TodoModel) {
        todoDao.addTodo(todo)
    }

    override suspend fun updateTodo(todo: TodoModel) {
        todoDao.updateTodo(todo)
    }

    override suspend fun deleteTodoById(todoId: Long) {
        todoDao.deleteTodo(todoId)
    }

}