package de.telma.todolist.data.model

data class Note(
    val id: Long,
    val title: String = "Untitled",
    val status: NoteStatus,
    val tasksList: List<NoteTask>
)

enum class NoteStatus(val status: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}