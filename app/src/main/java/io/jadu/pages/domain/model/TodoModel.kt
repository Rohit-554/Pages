package io.jadu.pages.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoModel (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val isTaskCompleted: Boolean,
    val task: String? = null,
    var date: Long = System.currentTimeMillis(),
)