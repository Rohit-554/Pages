package io.jadu.pages.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jadu.pages.domain.model.TodoModel
import io.jadu.pages.domain.repository.TodoRepository
import io.jadu.pages.domain.usecase.todoUseCases.AddTodoUseCase
import io.jadu.pages.domain.usecase.todoUseCases.DeleteTodoUseCase
import io.jadu.pages.domain.usecase.todoUseCases.GetAllTodosUseCase
import io.jadu.pages.domain.usecase.todoUseCases.UpdateTodoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getAllTodosUseCase: GetAllTodosUseCase
) :ViewModel(){

    fun addTodo(todo: TodoModel) = viewModelScope.launch {
        addTodoUseCase.invoke(todo)
    }

    fun updateTodo(todo: TodoModel) = viewModelScope.launch{
        updateTodoUseCase.invoke(todo)
    }

    fun deleteTodo(id:Long) = viewModelScope.launch{
        deleteTodoUseCase.invoke(id)
    }

    val getAllTodo:Flow<List<TodoModel>> = getAllTodosUseCase.invoke()
}