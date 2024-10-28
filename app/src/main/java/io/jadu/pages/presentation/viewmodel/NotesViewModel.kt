package io.jadu.pages.presentation.viewmodel

import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.usecase.AddNoteUseCase
import io.jadu.pages.domain.usecase.DeleteNotesUseCase
import io.jadu.pages.domain.usecase.GetNotesPaginatedUseCase
import io.jadu.pages.domain.usecase.UpdateNotesPositionUseCase
import io.jadu.pages.domain.usecase.UpdateNotesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val addNotesUseCase: AddNoteUseCase,
    private val updateNotesUseCase: UpdateNotesUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val getNotesPaginatedUseCase: GetNotesPaginatedUseCase,
    private val updateNotesPositionUseCase: UpdateNotesPositionUseCase
):ViewModel() {

    init {
        getNotesPaginated(10,0)
    }

    private val _notes = MutableStateFlow<List<Notes>>(emptyList())
    val notes: StateFlow<List<Notes>> get() = _notes

    fun addNotes(note: Notes) = viewModelScope.launch {
        addNotesUseCase.invoke(note)
    }

    fun updateNotes(title: String, description: String?, imageUri: String?, notesId: Long, color:String?) = viewModelScope.launch {
        updateNotesUseCase.invoke(title, description, imageUri, notesId,color)
    }

    fun deleteNotes(noteId: Long) = viewModelScope.launch {
        deleteNotesUseCase.invoke(noteId)
    }

    fun updateNotesPosition(id: Long, position: Int) = viewModelScope.launch {
        updateNotesPositionUseCase.invoke(id, position)
    }

    fun getNotesPaginated(limit: Int, offset: Int) {
        viewModelScope.launch {
            getNotesPaginatedUseCase(limit, offset).collect { newNotes ->
                _notes.value = newNotes
            }
        }
    }




}