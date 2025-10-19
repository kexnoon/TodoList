package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDateTime

class UpdateTaskStatusUseCase(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val clock: Clock
) {
    sealed interface Result {
        object SUCCESS : Result
        object FAILURE : Result
    }

    suspend operator fun invoke(noteId: Long, task: NoteTask, newStatus: NoteTaskStatus): Result {
        var result: Result = Result.FAILURE

        val updatedTask = task.copy(status = newStatus)
        if (taskRepository.updateTask(noteId, updatedTask)) {
            noteRepository.getNoteById(noteId).first()?.let {
                val timestamp = getTimestamp(LocalDateTime.now(clock))
                val isTimestampSet = noteRepository.updateNote(it.copy(lastUpdatedTimestamp = timestamp))
                if (isTimestampSet) result = Result.SUCCESS
            }
        }

        return result
    }

}