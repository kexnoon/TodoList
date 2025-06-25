package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.toNoteTaskEntity
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteTaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepositoryImpl(private val database: AppDatabase): TaskRepository {

    override suspend fun createNewTask(noteId: Long, title: String): Boolean = withContext(Dispatchers.IO) {
        val newTask = NoteTaskEntity(
            id = 0L,
            noteId = noteId,
            title = title,
            status = NoteTaskStatus.IN_PROGRESS.statusValue //default task status
        )
        return@withContext database.noteTaskDao().insertTask(newTask) != -1L
    }

    override suspend fun updateTask(noteId: Long, task: NoteTask): Boolean = withContext(Dispatchers.IO) {
        val entity = task.toNoteTaskEntity(noteId)
        return@withContext database.noteTaskDao().updateTask(entity) != -1
    }

    override suspend fun deleteTask(task: NoteTask): Boolean = withContext(Dispatchers.IO) {
        return@withContext database.noteTaskDao().deleteTask(task.id) != -1
    }
}