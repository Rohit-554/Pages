package io.jadu.pages.core

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.data.local.NotesDatabase
import io.jadu.pages.data.repository.NotesRepositoryImpl
import io.jadu.pages.domain.repository.NotesRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app:Application): NotesDatabase{
        return Room.databaseBuilder(app,NotesDatabase::class.java,"notes_db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideNotesDao(database: NotesDatabase): NotesDao {
        return database.notesDao()
    }

    @Provides
    @Singleton
    fun provideNotesRepository(taskDao: NotesDao): NotesRepository {
        return NotesRepositoryImpl(taskDao)
    }

    @Provides
    fun provideAddNoteUseCase(repository: NotesRepository): AddTaskUseCase {
        return AddTaskUseCase(repository)
    }

    @Provides
    fun provideUpdateNoteUseCase(repository: TaskRepository): UpdateTaskUseCase {
        return UpdateTaskUseCase(repository)
    }

    @Provides
    fun provideDeleteNoteUseCase(repository: TaskRepository): DeleteTaskUseCase {
        return DeleteTaskUseCase(repository)
    }

    @Provides
    fun provideGetNotesUseCase(repository: TaskRepository): GetTasksUseCase {
        return GetTasksUseCase(repository)
    }

    @Provides
    fun provideReorderNotesUseCase(repository: TaskRepository): ReorderTasksUseCase {
        return ReorderTasksUseCase(repository)
    }


}