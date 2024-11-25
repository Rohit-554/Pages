package io.jadu.pages.data.repository

import android.net.Uri
import androidx.compose.ui.graphics.Path
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao
): NotesRepository {

    override fun getNotesPaginated(): Flow<PagingData<Notes>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8   ,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { notesDao.getNotesPaginated() }
        ).flow
    }

    override suspend fun addNotes(note: Notes) {
        notesDao.addNotes(note)
    }

    override suspend fun updateNotes(title:String, description: String?, imageUri: List<Uri>?, drawingPaths: List<List<Pair<Path, PathProperties>>>? , notesId: Long, color:String?, isPinned:Boolean) {
        notesDao.updateNotes(notesId, title, description, imageUri, drawingPaths, color, isPinned)
    }

    override suspend fun deleteNotes(noteId: Long) {
        notesDao.deleteNote(noteId)
    }

    override suspend fun updateNotesPosition(id: Long, position: Int) {
        notesDao.updateNotesPosition(id, position)
    }

    override suspend fun searchNotes(searchText: String): Flow<List<Notes>> {
        return notesDao.searchNotes(searchText)
    }

    override fun getNotes(): Flow<List<Notes>> {
        return notesDao.getNotes()
    }

}