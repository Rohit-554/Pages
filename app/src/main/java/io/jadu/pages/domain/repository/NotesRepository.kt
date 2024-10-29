package io.jadu.pages.domain.repository

import android.net.Uri
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.domain.model.Notes
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotesPaginated(limit: Int, offset: Int): Flow<List<Notes>>
    suspend fun addNotes(note: Notes)
    suspend fun updateNotes(title: String, description: String?, imageUri: String?, notesId:Long, color: String?)
    suspend fun deleteNotes(noteId: Long)
    suspend fun updateNotesPosition(id: Long, position: Int)
    suspend fun searchNotes(searchText: String): Flow<List<Notes>>
}