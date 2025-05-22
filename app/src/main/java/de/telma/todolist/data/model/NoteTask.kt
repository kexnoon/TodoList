package de.telma.todolist.data.model

data class NoteTask(
    val id: Long,
    val title: String,
    val status: NoteTaskStatus
)

enum class NoteTaskStatus(val status: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}