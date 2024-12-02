package io.jadu.pages.core

import android.app.Application
import androidx.room.Room
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.ToolConfig
import com.google.ai.client.generativeai.type.generationConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.jadu.pages.BuildConfig
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.data.dao.TodoDao
import io.jadu.pages.data.local.NotesDatabase
import io.jadu.pages.data.repository.NotesRepositoryImpl
import io.jadu.pages.data.repository.OcrRepositoryImpl
import io.jadu.pages.data.repository.TodoRepositoryImpl
import io.jadu.pages.domain.repository.NotesRepository
import io.jadu.pages.domain.repository.OcrRepository
import io.jadu.pages.domain.repository.TodoRepository
import io.jadu.pages.domain.usecase.AddNoteUseCase
import io.jadu.pages.domain.usecase.DeleteNotesUseCase
import io.jadu.pages.domain.usecase.GenerateTextUseCase
import io.jadu.pages.domain.usecase.GetAllNotesUseCase
import io.jadu.pages.domain.usecase.GetNotesPaginatedUseCase
import io.jadu.pages.domain.usecase.SearchNoteUseCase
import io.jadu.pages.domain.usecase.UpdateNotesPositionUseCase
import io.jadu.pages.domain.usecase.UpdateNotesUseCase
import io.jadu.pages.domain.usecase.todoUseCases.AddTodoUseCase
import io.jadu.pages.domain.usecase.todoUseCases.DeleteTodoUseCase
import io.jadu.pages.domain.usecase.todoUseCases.GetAllTodosUseCase
import io.jadu.pages.domain.usecase.todoUseCases.UpdateTodoUseCase
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
    fun provideAddNoteUseCase(repository: NotesRepository): AddNoteUseCase {
        return AddNoteUseCase(repository)
    }

    @Provides
    fun provideUpdateNoteUseCase(repository: NotesRepository): UpdateNotesUseCase {
        return UpdateNotesUseCase(repository)
    }

    @Provides
    fun provideDeleteNoteUseCase(repository: NotesRepository): DeleteNotesUseCase {
        return DeleteNotesUseCase(repository)
    }

    @Provides
    fun provideGetNotesUseCase(repository: NotesRepository): GetNotesPaginatedUseCase {
        return GetNotesPaginatedUseCase(repository)
    }

    @Provides
    fun provideReorderNotesUseCase(repository: NotesRepository): UpdateNotesPositionUseCase {
        return UpdateNotesPositionUseCase(repository)
    }

    @Provides
    @Singleton
    fun searchNoteUseCase(repository: NotesRepository): SearchNoteUseCase {
        return SearchNoteUseCase(repository)
    }
    //Todos

    @Provides
    @Singleton
    fun provideTodoDao(database: NotesDatabase): TodoDao {
        return database.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoRepository(todoDao: TodoDao): TodoRepository {
        return TodoRepositoryImpl(todoDao)
    }

    @Provides
    fun provideAddTodoUseCase(repository: TodoRepository): AddTodoUseCase {
        return AddTodoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateTodoUseCase(repository: TodoRepository): UpdateTodoUseCase {
        return UpdateTodoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteTodoUseCase(repository: TodoRepository): DeleteTodoUseCase {
        return DeleteTodoUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllTodosUseCase(repository: TodoRepository): GetAllTodosUseCase {
        return GetAllTodosUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAllNotesUseCase(repository: NotesRepository): GetAllNotesUseCase {
        return GetAllNotesUseCase(repository)
    }

    @Provides
    fun provideUtils(): Utils = Utils()

    @Provides
    fun provideGenerativeModel(): GenerativeModel = GenerativeModel(
        modelName = Constants.MODEL,
        apiKey = BuildConfig.GEMINI_KEY,
        generationConfig = generationConfig {
            temperature = 0.15f
            topK = 32
            topP = 1f
            maxOutputTokens = 8192
        },
        safetySettings = listOf(
            SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.NONE),
            SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.NONE),
            SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.NONE),
        ),

    )

    @Provides
    fun provideContentRepository(
        model: GenerativeModel,
        utils: Utils
    ): OcrRepository = OcrRepositoryImpl(model, utils)

    @Provides
    fun provideGenerateTextUseCase(repository: OcrRepository): GenerateTextUseCase =
        GenerateTextUseCase(repository)



}