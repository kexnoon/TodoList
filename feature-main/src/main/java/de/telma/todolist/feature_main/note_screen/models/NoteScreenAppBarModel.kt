package de.telma.todolist.feature_main.note_screen.models

import de.telma.todolist.component_notes.model.NoteStatus

data class NoteScreenAppBarModel(
    val noteId: Long,
    val title: String,
    val isComplete: Boolean,
)
