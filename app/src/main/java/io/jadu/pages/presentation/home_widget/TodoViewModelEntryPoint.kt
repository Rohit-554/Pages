package io.jadu.pages.presentation.home_widget

import android.content.Context
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jadu.pages.domain.repository.TodoRepository

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TodoUseCaseEntryPoint {
    fun todoUseCase(): TodoRepository
}

fun getTodoRepository(context: Context): TodoRepository {
    val hiltEntryPoint = EntryPointAccessors.fromApplication(
        context.applicationContext,
        TodoUseCaseEntryPoint::class.java
    )
    return hiltEntryPoint.todoUseCase()
}
