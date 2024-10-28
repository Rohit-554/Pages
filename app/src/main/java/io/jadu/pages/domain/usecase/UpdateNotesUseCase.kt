package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.repository.NotesRepository

class UpdateNotesUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(title:String, description: String?, imageUri: String?, notesId: Long) {
        repository.updateNotes(title, description, imageUri, notesId)
    }
}