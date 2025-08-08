package de.telma.todolist.feature_main.main_screen.model

enum class NotesListItemState { IN_PROGRESS, COMPLETE }

data class NotesListItemModel(
    val id: Long,
    val title: String,
    val status: NotesListItemState,
    val lastUpdatedTimestamp: String,
    val numberOfTasks: Int
)
