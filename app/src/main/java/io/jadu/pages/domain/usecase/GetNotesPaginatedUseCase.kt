package io.jadu.pages.domain.usecase

import androidx.paging.PagingData
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class GetNotesPaginatedUseCase(private val repository: NotesRepository) {
    operator fun invoke(): Flow<PagingData<Notes>> {
        return repository.getNotesPaginated()
    }
}
