package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteTaskEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.database.sqlite.SQLiteConstraintException

class TaskRepositoryImpl(private val database: AppDatabase): TaskRepository {

    override suspend fun createNewTask(noteId: Long, title: String): Boolean = withContext(Dispatchers.IO) {
        val newTask = NoteTaskEntity(
            id = 0L,
            noteId = noteId,
            title = title,
            status = NoteTaskStatus.IN_PROGRESS.statusValue
        )
        try {
            database.noteTaskDao().insertTask(newTask)
            return@withContext true
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            return@withContext false
        }
    }

    override suspend fun updateTask(noteId: Long, task: NoteTask): Boolean = withContext(Dispatchers.IO) {
        val entity = task.toNoteTaskEntity(noteId)
        try {
            val result = database.noteTaskDao().updateTask(entity)
            return@withContext result == 1
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            return@withContext false
        }
    }

    override suspend fun deleteTask(task: NoteTask): Boolean = withContext(Dispatchers.IO) {
        try {
            val result = database.noteTaskDao().deleteTask(task.id)
            return@withContext result == 1
        } catch (e: SQLiteConstraintException) {
            e.printStackTrace()
            return@withContext false
        }
    }
}