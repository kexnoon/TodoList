package de.telma.todolist.component_notes.model

data class NoteTask(
    val id: Long,
    val title: String,
    val status: NoteTaskStatus
) {
    fun getOppositeStatus(): NoteTaskStatus {
        if (status == NoteTaskStatus.IN_PROGRESS)
            return NoteTaskStatus.COMPLETE
        else
            return NoteTaskStatus.IN_PROGRESS
    }
}

enum class NoteTaskStatus(val statusValue: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}