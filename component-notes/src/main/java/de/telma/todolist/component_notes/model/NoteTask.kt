package de.telma.todolist.component_notes.model

data class NoteTask(
    val id: Long,
    val title: String,
    val status: NoteTaskStatus
)

enum class NoteTaskStatus(val statusValue: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}