package de.telma.todolist.component_notes.utils

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.storage.database.entity.FolderEntity
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteTaskEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks

internal fun List<NoteWithTasks>.toNotesList(): List<Note> = this.map {
    it.toNote()
}

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
        },
        createdTimestamp = this.note.createdTimestamp,
        lastUpdatedTimestamp = this.note.lastUpdatedTimestamp,
        folderId = this.note.folderId
    )
}

internal fun String.toNoteStatus(): NoteStatus {
    return try {
        NoteStatus.valueOf(this.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Error while converting String to NoteStatus: '$this'", e)
    }
}

internal fun String.toNoteTaskStatus(): NoteTaskStatus {
    return try {
        NoteTaskStatus.valueOf(this.uppercase())
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Error while converting String to NoteStatus: '$this'", e)
    }

}

internal fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        title = this.title,
        status = this.status.statusValue,
        createdTimestamp = this.createdTimestamp,
        lastUpdatedTimestamp = this.lastUpdatedTimestamp,
        folderId = this.folderId
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

internal fun NoteTaskEntity.toNoteTask() : NoteTask {
    return NoteTask(
        id = this.id,
        title = this.title,
        status = this.status.toNoteTaskStatus()
    )
}

internal fun FolderEntity.toFolder(): Folder {
    return Folder(
        id = this.id,
        name = this.name,
        lastUpdatedTimestamp = this.lastUpdatedTimestamp
    )
}

internal fun List<FolderEntity>.toFoldersList(): List<Folder> = this.map(FolderEntity::toFolder)
