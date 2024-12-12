package io.jadu.pages.domain.usecase

import android.net.Uri
import androidx.compose.ui.graphics.Path
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import io.jadu.pages.domain.repository.NotesRepository

class UpdateNotesUseCase(private val repository: NotesRepository) {
    suspend operator fun invoke(title:String, description: String?, imageUri: List<Uri>?, drawingPaths: List<List<Pair<Path, PathProperties>>>?, notesId: Long, color:String?, isPinned:Boolean, isNotesSaved:Boolean) {
        repository.updateNotes(title, description, imageUri, drawingPaths, notesId, color, isPinned, isNotesSaved)
    }
}