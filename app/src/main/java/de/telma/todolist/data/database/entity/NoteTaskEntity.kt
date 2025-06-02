package de.telma.todolist.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import de.telma.todolist.data.database.NoteTaskStatusConverter
import de.telma.todolist.data.model.NoteTaskStatus

@Entity(
    tableName = "note_tasks",
    foreignKeys = [
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["noteId"])]
)
@TypeConverters(NoteTaskStatusConverter::class)
data class NoteTaskEntity(
    @PrimaryKey
    val id: Long,
    val noteId: Long,
    val title: String,
    val status: NoteTaskStatus
)