package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class CreateNewTaskUseCase(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data class SUCCESS(val newNoteId: Long): Result
        data object FAILURE: Result
    }

    suspend operator fun invoke(note: Note, title: String): Result {
        var result: Result = Result.FAILURE

        if(taskRepository.createNewTask(note.id, title)) {
            val isNoteUpdated = noteRepository.updateNote(
                note.copy(lastUpdatedTimestamp = getTimestamp(LocalDateTime.now(clock)))
            )
            result = if (isNoteUpdated) Result.SUCCESS(note.id) else Result.FAILURE
        }

        return result
    }


}