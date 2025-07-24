package de.telma.todolist.component_notes.useCase

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

    suspend operator fun invoke(noteId: Long, task: NoteTask, newStatus: NoteTaskStatus): Boolean {
        var result = false

        val updatedTask = task.copy(status = newStatus)
        if (taskRepository.updateTask(noteId, updatedTask)) {
            noteRepository.getNoteById(noteId).first()?.let {
                val timestamp = getTimestamp(LocalDateTime.now(clock))
                result = noteRepository.updateNote(it.copy(lastUpdatedTimestamp = timestamp))
            }
        }

        return result
    }

}