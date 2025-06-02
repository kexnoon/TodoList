package de.telma.todolist.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import de.telma.todolist.data.model.NoteStatus
import de.telma.todolist.data.database.NoteStatusConverter

@Entity(tableName = "notes")
@TypeConverters(NoteStatusConverter::class)
data class NoteEntity(
    @PrimaryKey
    val id: Long,

    @ColumnInfo(defaultValue = "Untitled")
    val title: String = "Untitled",
    val status: NoteStatus
)