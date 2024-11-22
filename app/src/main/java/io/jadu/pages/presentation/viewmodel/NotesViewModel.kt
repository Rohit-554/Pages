package io.jadu.pages.presentation.viewmodel


import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.domain.usecase.AddNoteUseCase
import io.jadu.pages.domain.usecase.DeleteNotesUseCase
import io.jadu.pages.domain.usecase.GetNotesPaginatedUseCase
import io.jadu.pages.domain.usecase.SearchNoteUseCase
import io.jadu.pages.domain.usecase.UpdateNotesPositionUseCase
import io.jadu.pages.domain.usecase.UpdateNotesUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
    val notes = searchText
        .combine(_notes) { searchText, notes ->
            if (searchText.isBlank()) {
                notes
            } else {
                notes.filter { it.doesMatchSearchQuery(searchText) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            _notes.value
        )


    private val _updatedNotes = MutableStateFlow<List<Notes>>(emptyList())
    val updatedNotes: StateFlow<List<Notes>> get() = _updatedNotes

    private val _imageUriList = MutableStateFlow<List<Uri>>(emptyList())
    val imageUriList: StateFlow<List<Uri>> get() = _imageUriList

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> get() = _searchText

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> get() = _isSearching

    private val _notesState = MutableStateFlow(NotesState())
    val notesState: StateFlow<NotesState> get() = _notesState

    val notesFlow: Flow<PagingData<Notes>> = getNotesPaginatedUseCase()
        .cachedIn(viewModelScope)
        .onEach { _isSearching.update { true } }
        .combine(searchText) { pagingData, searchText ->
            if (searchText.isBlank()) {
                pagingData
            } else {
                pagingData.filter { it.doesMatchSearchQuery(searchText) }
            }
        }
        .onEach { _isSearching.update { false } }

    private val _drawingPathList =
        MutableStateFlow<List<List<Pair<Path, PathProperties>>>>(emptyList())
    val drawingPathList: StateFlow<List<List<Pair<Path, PathProperties>>>> get() = _drawingPathList

    fun onSearchTextChanged(searchText: String) {
        Log.d("SearchText", "SearchText: $searchText")
        _searchText.value = searchText
    }

    fun addImageUris(imageUri: Uri) {
        _imageUriList.value += imageUri
    }

    fun removeImageUri(imageUri: Uri) {
        _imageUriList.value -= imageUri
    }

    fun clearImageUriList() {
        _imageUriList.value = emptyList()
    }

    fun addNotesState(notes: NotesState) {
        _notesState.value = notes
    }

    fun removeNotesStates() {
        _notesState.value = NotesState()
    }

    fun addNotes(note: Notes) = viewModelScope.launch {
        addNotesUseCase.invoke(note)
    }


    fun updateNotes(
        title: String,
        description: String?,
        imageUri: List<Uri>?,
        drawingPaths: List<List<Pair<Path, PathProperties>>>?,
        notesId: Long,
        color: String?,
        isPinned: Boolean
    ) = viewModelScope.launch {
        updateNotesUseCase.invoke(
            title,
            description,
            imageUri,
            drawingPaths,
            notesId,
            color,
            isPinned
        )
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

    /*fun getNotesPaginated(limit: Int, offset: Int) {
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
                _notes.value = currentNotes.filter { it.id in newNoteIds || currentNotes.indexOf(it) < currentNotes.size }
            }
        }
    }*/
}

data class NotesState(
    val title: String = "",
    val description: String = "",
    val color: Color = Color.Black,
    val isPinned: Boolean = false,
    val shouldScroll: Boolean = false
)