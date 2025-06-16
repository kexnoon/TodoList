package de.telma.todolist.component_notes.model

data class Note(
    val id: Long,
    val title: String = "Untitled",
    val status: NoteStatus,
    val tasksList: List<NoteTask>
)

enum class NoteStatus(val statusValue: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}

