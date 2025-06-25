package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteTaskEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks

internal fun List<NoteWithTasks>.toNotesList(): List<Note> = this.map { it.toNote() }

internal fun NoteWithTasks.toNote(): Note {
    return Note(
        id = this.note.id,
        title = this.note.title,
        status = this.note.status.toNoteStatus(),
        tasksList = this.tasks.map { task ->
            NoteTask(
                id = task.id,
                title = task.title,
                status = task.status.toNoteTaskStatus()
            )
        }
    )
}

internal fun String.toNoteStatus(): NoteStatus {
    return NoteStatus.valueOf(this)
}

internal fun String.toNoteTaskStatus(): NoteTaskStatus {
    return NoteTaskStatus.valueOf(this)
}

internal fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        title = this.title,
        status = this.status.statusValue
    )
}

internal fun NoteTask.toNoteTaskEntity(parentId: Long): NoteTaskEntity {
    return NoteTaskEntity(
        id = this.id,
        noteId = parentId,
        title = this.title,
        status = this.status.statusValue
    )
}