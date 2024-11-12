package io.jadu.pages.presentation.viewmodel

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.domain.usecase.AddNoteUseCase
import io.jadu.pages.domain.usecase.DeleteNotesUseCase
import io.jadu.pages.domain.usecase.GetNotesPaginatedUseCase
import io.jadu.pages.domain.usecase.SearchNoteUseCase
import io.jadu.pages.domain.usecase.UpdateNotesPositionUseCase
import io.jadu.pages.domain.usecase.UpdateNotesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val addNotesUseCase: AddNoteUseCase,
    private val updateNotesUseCase: UpdateNotesUseCase,
    private val deleteNotesUseCase: DeleteNotesUseCase,
    private val getNotesPaginatedUseCase: GetNotesPaginatedUseCase,
    private val updateNotesPositionUseCase: UpdateNotesPositionUseCase,
    private val searchNotesUseCase: SearchNoteUseCase
) : ViewModel() {

    /* init {
         getNotesPaginated(10, 0)
     }*/

    private var currentOffset = 0 // Tracks the current offset
    private val limit = 10
    private val _notes = MutableStateFlow<List<Notes>>(emptyList())
    val notes: StateFlow<List<Notes>> get() = _notes

    private val _updatedNotes = MutableStateFlow<List<Notes>>(emptyList())
    val updatedNotes: StateFlow<List<Notes>> get() = _updatedNotes

    private val _imageUriList = MutableStateFlow<List<Uri?>>(emptyList())
    val imageUriList: StateFlow<List<Uri?>> get() = _imageUriList

    private val _drawingPathList = MutableStateFlow<List<List<Pair<Path, PathProperties>>>>(emptyList())
    val drawingPathList: StateFlow<List<List<Pair<Path, PathProperties>>>> get() = _drawingPathList

    fun addImageUris(imageUri: Uri) {
        _imageUriList.value += imageUri
    }

    fun removeImageUri(imageUri: Uri) {
        _imageUriList.value -= imageUri
    }

    fun clearImageUriList() {
        _imageUriList.value = emptyList()
    }

    fun addDrawingPath(drawPath: List<Pair<Path, PathProperties>>) {
        _drawingPathList.value += listOf(drawPath)
    }

    fun removeDrawingPath(drawPath: List<Pair<Path, PathProperties>>) {
        _drawingPathList.value -= listOf(drawPath)
    }


    fun clearDrawingPathList() {
        _drawingPathList.value = emptyList()
    }

    fun addNotes(note: Notes) = viewModelScope.launch {
        addNotesUseCase.invoke(note)
    }


    fun updateNotes(
        title: String,
        description: String?,
        imageUri: String?,
        notesId: Long,
        color: String?,
        isPinned: Boolean
    ) = viewModelScope.launch {
        updateNotesUseCase.invoke(title, description, imageUri, notesId, color, isPinned)
        //getNotesPaginated(10, 0)
    }

    fun deleteNotes(noteId: Long) = viewModelScope.launch {
        deleteNotesUseCase.invoke(noteId)
        _notes.value = _notes.value.filter { it.id != noteId }
    }

    fun searchNotes(searchText: String) = viewModelScope.launch {
        Log.d("SearchText", "SearchText: $searchText")
        searchNotesUseCase.invoke(searchText).collect { newNotes ->
            _notes.value = newNotes
        }
    }

    fun updateNotesPosition(id: Long, position: Int) = viewModelScope.launch {
        updateNotesPositionUseCase.invoke(id, position)
    }

    fun swapNotes(index1: Int, index2: Int) {
        val currentNotes = _notes.value.toMutableList()
        Collections.swap(currentNotes, index1, index2)
        _notes.value = currentNotes
    }
    fun getNotesPaginated(limit: Int, offset: Int) {
        viewModelScope.launch {
            getNotesPaginatedUseCase(limit, offset).collect { newNotes ->
                val currentNotes = _notes.value.toMutableList()
                newNotes.forEach { newNote ->
                    val existingIndex = currentNotes.indexOfFirst { it.id == newNote.id }
                    if (existingIndex != -1) {
                        currentNotes[existingIndex] = newNote
                    } else {
                        currentNotes.add(newNote)
                    }
                }

                val newNoteIds = newNotes.map { it.id }.toSet()
                _notes.value =
                    currentNotes.filter { it.id in newNoteIds || currentNotes.indexOf(it) < currentNotes.size }
            }
        }
    }


}