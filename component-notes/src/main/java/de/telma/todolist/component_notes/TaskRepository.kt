package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.NoteTask

interface TaskRepository {

    suspend fun createNewTask(noteId: Long, title: String): Boolean

    suspend fun updateTask(noteId: Long, task: NoteTask): Boolean

    suspend fun deleteTask(task: NoteTask): Boolean

}