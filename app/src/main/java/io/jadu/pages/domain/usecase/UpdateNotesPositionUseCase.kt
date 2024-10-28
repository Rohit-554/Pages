package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.repository.NotesRepository

class UpdateNotesPositionUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(noteId: Long, position: Int) {
        repository.updateNotesPosition(noteId, position)
    }
}