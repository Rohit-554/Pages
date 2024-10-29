package io.jadu.pages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.data.dao.TodoDao
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.TodoModel

@Database(entities = [Notes::class,TodoModel::class], version = 2, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun todoDao(): TodoDao
}
