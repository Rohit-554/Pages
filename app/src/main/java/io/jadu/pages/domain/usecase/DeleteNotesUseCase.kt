package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.repository.NotesRepository

class DeleteNotesUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(noteId: Long) {
        repository.deleteNotes(noteId)
    }
}