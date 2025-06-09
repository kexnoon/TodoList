package de.telma.todolist.data

import de.telma.todolist.data.database.entity.NoteWithTasks
import de.telma.todolist.data.model.Note
import de.telma.todolist.data.model.NoteTask

fun List<NoteWithTasks>.toNotesList(): List<Note> = this.map { it.toNote() }

fun NoteWithTasks.toNote(): Note {
    return Note(
        id = this.note.id,
        title = this.note.title,
        status = this.note.status,
        tasksList = this.tasks.map { task ->
            NoteTask(
                id = task.id,
                title = task.title,
                status = task.status
            )
        }
    )
}
