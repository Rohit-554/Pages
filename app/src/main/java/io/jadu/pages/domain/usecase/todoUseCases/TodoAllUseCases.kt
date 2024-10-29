package io.jadu.pages.domain.usecase.todoUseCases

import io.jadu.pages.domain.model.TodoModel
import io.jadu.pages.domain.repository.TodoRepository

class AddTodoUseCase(private val repository: TodoRepository){
    suspend operator fun invoke(todo: TodoModel){
        repository.addTodo(todo)
    }
}

class UpdateTodoUseCase(private val repository: TodoRepository){
    suspend operator fun invoke(todo: TodoModel){
        repository.updateTodo(todo)
    }
}

class DeleteTodoUseCase(private val repository: TodoRepository){
    suspend operator fun invoke(todoId: Long){
        repository.deleteTodoById(todoId)
    }
}

class GetAllTodosUseCase(private val repository: TodoRepository){
    operator fun invoke() = repository.getAllTodos()
}

