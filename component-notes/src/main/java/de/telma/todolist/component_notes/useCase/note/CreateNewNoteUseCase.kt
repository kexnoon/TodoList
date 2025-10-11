package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

// TODO: write unit tests
class CreateNewNoteUseCase(
    private val noteRepository: NoteRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data class SUCCESS(val newNoteId: Long): Result
        data object FAILURE: Result
    }

    suspend operator fun invoke(title: String): Result {
        try {
            val newNoteId = noteRepository.createNewNote(
                title, getTimestamp(LocalDateTime.now(clock))
            )
            return Result.SUCCESS(newNoteId)
        } catch (e: Exception) {
            return Result.FAILURE
        }
    }
}