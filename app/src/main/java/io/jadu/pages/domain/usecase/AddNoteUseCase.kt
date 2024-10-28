package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.repository.NotesRepository

class AddNoteUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Notes) {
        repository.addNotes(note)
    }
}