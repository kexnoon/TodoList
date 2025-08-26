package de.telma.todolist.feature_main.main_screen.models

enum class NotesListItemState { IN_PROGRESS, COMPLETE }

data class NotesListItemModel(
    val id: Long,
    val title: String,
    val status: NotesListItemState,
    val lastUpdatedTimestamp: String,
    val numberOfTasks: Int,
    var isSelected: Boolean = false
)
