package io.jadu.pages.data.dao

import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.compose.ui.graphics.Path
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.jadu.pages.domain.model.Notes
import io.jadu.pages.domain.model.PathProperties
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY position ASC")
    fun getNotesPaginated(): PagingSource<Int, Notes>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotes(note: Notes)

    @Query("UPDATE notes SET title = :title, description = :description, imageUri = :imageUris, color = :color, isPinned = :isPinned, drawingPaths = :drawingPaths WHERE id = :notesId")
    suspend fun updateNotes(
        notesId: Long,
        title: String,
        description: String?,
        imageUris: List<Uri>?,
        drawingPaths: List<List<Pair<Path, PathProperties>>>? = null,
        color: String?,
        isPinned: Boolean
    )

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("UPDATE notes SET position = :position WHERE id = :id")
    suspend fun updateNotesPosition(id: Long, position: Int)

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchText || '%' OR description LIKE '%' || :searchText || '%'")
    fun searchNotes(searchText: String): Flow<List<Notes>>

}
