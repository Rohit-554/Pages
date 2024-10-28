package io.jadu.pages.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.domain.model.Notes

@Database(entities = [Notes::class], version = 1, exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}
