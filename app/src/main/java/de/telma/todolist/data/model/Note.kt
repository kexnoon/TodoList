package de.telma.todolist.data.model

import de.telma.todolist.data.database.entity.NoteWithTasks

data class Note(
    val id: Long,
    val title: String = "Untitled",
    val status: NoteStatus,
    val tasksList: List<NoteTask>
) {
    fun fromNoteWithTask(noteWithTasks: NoteWithTasks): Note {
        return Note(
            id = noteWithTasks.note.id,
            title = noteWithTasks.note.title,
            status = noteWithTasks.note.status,
            tasksList = noteWithTasks.tasks.map { task ->
                NoteTask(
                    id = task.id,
                    title = task.title,
                    status = task.status
                )
            }
        )
    }
}

enum class NoteStatus(val statusValue: String) {
    IN_PROGRESS("in_progress"), COMPLETE("complete")
}

