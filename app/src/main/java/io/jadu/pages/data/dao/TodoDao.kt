package io.jadu.pages.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.TodoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todo: TodoModel)

    @Update
    suspend fun updateTodo(todo: TodoModel)

    @Query("DELETE FROM todo WHERE id = :todoId")
    suspend fun deleteTodo(todoId: Long)

    //get all todos
    @Query("SELECT * FROM todo")
    fun getAllTodos(): Flow<List<TodoModel>>

}