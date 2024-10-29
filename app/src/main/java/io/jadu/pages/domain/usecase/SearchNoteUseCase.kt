package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.repository.NotesRepository

class SearchNoteUseCase(
    private val repository: NotesRepository
) {
    suspend operator fun invoke(searchText: String) = repository.searchNotes(searchText)
}