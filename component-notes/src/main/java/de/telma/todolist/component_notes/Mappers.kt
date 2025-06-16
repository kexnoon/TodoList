package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.storage.database.entity.NoteWithTasks

internal fun List<NoteWithTasks>.toNotesList(): List<Note> = this.map { it.toNote() }

internal fun NoteWithTasks.toNote(): Note {
    return Note(
        id = this.note.id,
        title = this.note.title,
        status = this.note.status.toNoteStatus(this.note.id),
        tasksList = this.tasks.map { task ->
            NoteTask(
                id = task.id,
                title = task.title,
                status = task.status.toNoteTaskStatus(this.note.id)
            )
        }
    )
}

internal fun String.toNoteStatus(noteId: Long): NoteStatus {
    return NoteStatus.entries.find { it.statusValue == this } ?: throw RuntimeException("Wrong NoteStatus! (Note id: $noteId)")
}

internal fun String.toNoteTaskStatus(noteId: Long): NoteTaskStatus {
    return NoteTaskStatus.entries.find { it.statusValue == this } ?: throw RuntimeException("Wrong NoteStatusStatus! (NoteTask id: $noteId)")
}