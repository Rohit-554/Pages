package io.jadu.pages.domain.usecase

import io.jadu.pages.domain.repository.NotesRepository

class GetNotesPaginatedUseCase(private val repository: NotesRepository) {
    operator fun invoke(limit: Int, offset: Int) = repository.getNotesPaginated(limit, offset)
}