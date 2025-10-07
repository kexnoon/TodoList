package de.telma.todolist.feature_main.note_screen.models

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus

data class TaskItemModel(
    val id: Long,
    val title: String,
    val isCompleted: Boolean
)

fun NoteTask.toTaskItemModel() = TaskItemModel(
    id = id,
    title = title,
    isCompleted = this.status == NoteTaskStatus.COMPLETE
)
