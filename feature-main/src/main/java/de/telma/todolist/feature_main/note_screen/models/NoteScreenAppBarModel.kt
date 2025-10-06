package de.telma.todolist.feature_main.note_screen.models

data class NoteScreenAppBarModel(
    val noteId: Long,
    val title: String,
    val isComplete: Boolean,
)
