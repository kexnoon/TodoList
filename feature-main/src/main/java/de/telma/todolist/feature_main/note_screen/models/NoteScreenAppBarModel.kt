package de.telma.todolist.feature_main.note_screen.models

data class NoteScreenAppBarModel(
    val noteId: Long,
    val title: String,
    val isComplete: Boolean,
    val folderLabel: String = "No folder",
    val selectedFolderId: Long? = null,
)
