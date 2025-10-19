package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class UpdateNoteStatusUseCase(
    private val repository: NoteRepository,
    private val clock: Clock
) {
    sealed interface Result {
        object Success : Result
        object Failure : Result
    }

    suspend operator fun invoke(note: Note, newStatus: NoteStatus): Result {
        val timestamp = getTimestamp(LocalDateTime.now(clock))
        val updatedNote = note.copy(status = newStatus, lastUpdatedTimestamp = timestamp)
        return if (repository.updateNote(updatedNote)) {
            Result.Success
        } else {
            Result.Failure
        }
    }
}