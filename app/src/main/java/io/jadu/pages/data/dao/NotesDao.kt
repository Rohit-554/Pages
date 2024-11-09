package io.jadu.pages.data.dao

import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.jadu.pages.domain.model.Notes
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes ORDER BY position ASC LIMIT :limit OFFSET :offset")
    fun getNotesPaginated(limit: Int, offset: Int): Flow<List<Notes>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotes(note: Notes)

    @Query("UPDATE notes SET title = :title, description = :description, imageUri = :imageUri, color = :color, isPinned = :isPinned  WHERE id = :notesId")
    suspend fun updateNotes(notesId: Long, title: String, description: String?, imageUri: String?, color: String?, isPinned:Boolean)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)

    @Query("UPDATE notes SET position = :position WHERE id = :id")
    suspend fun updateNotesPosition(id: Long, position: Int)

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchText || '%' OR description LIKE '%' || :searchText || '%'")
    fun searchNotes(searchText: String): Flow<List<Notes>>

}
