package io.jadu.pages.domain.repository

import android.net.Uri
import androidx.compose.ui.graphics.Path
import androidx.paging.PagingData
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotesPaginated(): Flow<PagingData<Notes>>
    suspend fun addNotes(note: Notes)
    suspend fun updateNotes(
        title: String,
        description: String?,
        imageUri: List<Uri>?,
        drawingPaths: List<List<Pair<Path, PathProperties>>>?,
        notesId: Long,
        color: String?,
        isPinned: Boolean,
        isNoteSaved: Boolean
    )

    suspend fun deleteNotes(noteId: Long)
    suspend fun updateNotesPosition(id: Long, position: Int)
    suspend fun searchNotes(searchText: String): Flow<List<Notes>>
    fun getNotes(): Flow<List<Notes>>
}