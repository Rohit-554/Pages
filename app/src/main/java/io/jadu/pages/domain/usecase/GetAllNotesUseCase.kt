package io.jadu.pages.domain.usecase

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.paging.PagingData
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow


class GetAllNotesUseCase(
    private val repository: NotesRepository
) {

    operator fun invoke(): Flow<List<Notes>> {
        return repository.getNotes()
    }
}