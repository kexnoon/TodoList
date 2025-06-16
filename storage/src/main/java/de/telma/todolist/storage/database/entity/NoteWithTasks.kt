package de.telma.todolist.storage.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NoteWithTasks(
    @Embedded
    val note: NoteEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val tasks: List<NoteTaskEntity>
)