package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.repository.TaskRepository

class RenameTaskUseCase(private val repository: TaskRepository) {

    suspend operator fun invoke(noteId: Long, task: NoteTask, newTitle: String): Boolean {
        val updatedTask = task.copy(title = newTitle)
        return repository.updateTask(noteId, updatedTask)
    }

}