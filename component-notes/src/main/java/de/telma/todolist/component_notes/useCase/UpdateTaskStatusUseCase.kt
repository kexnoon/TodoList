package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.TaskRepository

class UpdateTaskStatusUseCase(private val repository: TaskRepository) {

    suspend operator fun invoke(noteId: Long, task: NoteTask, newStatus: NoteTaskStatus): Boolean {
        val updatedTask = task.copy(status = newStatus)
        return repository.updateTask(noteId, updatedTask)
    }

}