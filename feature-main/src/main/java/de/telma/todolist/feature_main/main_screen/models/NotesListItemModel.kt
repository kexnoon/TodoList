package de.telma.todolist.feature_main.main_screen.models

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus

enum class NotesListItemState { IN_PROGRESS, COMPLETE }

data class NotesListItemModel(
    val id: Long,
    val title: String,
    val status: NotesListItemState,
    val lastUpdatedTimestamp: String,
    val numberOfTasks: Int,
    var isSelected: Boolean = false
)

fun Note.toNotesListItemModel(): NotesListItemModel {
    return NotesListItemModel(
        id = this.id,
        title = this.title,
        status = when (this.status) {
            NoteStatus.IN_PROGRESS -> NotesListItemState.IN_PROGRESS
            NoteStatus.COMPLETE -> NotesListItemState.COMPLETE
        },
        lastUpdatedTimestamp = this.lastUpdatedTimestamp,
        numberOfTasks = this.tasksList.size
    )
}
