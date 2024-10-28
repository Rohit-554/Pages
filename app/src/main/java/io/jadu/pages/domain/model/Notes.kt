package io.jadu.pages.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var title: String,
    var description: String? = null,
    var position: Int = 0,
    var imageUri: String? = null,
    var isPinned: Boolean = false,
    var date: Long = System.currentTimeMillis(),
    var color: String? = null
)