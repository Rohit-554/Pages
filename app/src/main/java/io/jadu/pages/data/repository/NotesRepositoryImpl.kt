package io.jadu.pages.data.repository

import io.jadu.pages.data.dao.NotesDao
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val notesDao: NotesDao
): NotesRepository {
    override fun getNotesPaginated(limit: Int, offset: Int): Flow<List<Notes>> {
        return notesDao.getNotesPaginated(limit, offset)
    }

    override suspend fun addNotes(note: Notes) {
        notesDao.addNotes(note)
    }

    override suspend fun updateNotes(title:String, description: String?, imageUri: String?, notesId: Long, color:String?, isPinned:Boolean) {
        notesDao.updateNotes(notesId, title, description, imageUri, color, isPinned)
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

}