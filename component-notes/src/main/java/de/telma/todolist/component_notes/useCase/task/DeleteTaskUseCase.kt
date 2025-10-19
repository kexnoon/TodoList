package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.repository.TaskRepository

class DeleteTaskUseCase(
    val repository: TaskRepository
) {
    sealed interface Result {
        object SUCCESS : Result
        object FAILURE : Result
    }

    suspend operator fun invoke(task: NoteTask): Result {
        return if (repository.deleteTask(task)) Result.SUCCESS else Result.FAILURE
    }
}