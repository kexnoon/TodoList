package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.repository.TaskRepository

class DeleteTaskUseCase(
    val repository: TaskRepository
) {
    suspend operator fun invoke(task: NoteTask): Boolean {
        return repository.deleteTask(task)
    }
}