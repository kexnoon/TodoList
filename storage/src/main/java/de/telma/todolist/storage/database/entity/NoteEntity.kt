package de.telma.todolist.storage.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(defaultValue = "Untitled")
    val title: String = "Untitled",
    val status: String,
    val lastUpdatedTimestamp: String
)