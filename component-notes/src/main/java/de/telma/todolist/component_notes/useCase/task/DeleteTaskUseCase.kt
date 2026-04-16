package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import java.time.Clock

class DeleteTaskUseCase(
    private val repository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        object SUCCESS : Result
        object FAILURE : Result
    }

    suspend operator fun invoke(noteId: Long, task: NoteTask): Result {
        return if (repository.deleteTask(task)) Result.SUCCESS else Result.FAILURE
    }
}
