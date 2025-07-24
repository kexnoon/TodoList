package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class CreateNewNoteUseCase(
    private val noteRepository: NoteRepository,
    private val clock: Clock
) {
    suspend operator fun invoke(title: String): Boolean {
        return noteRepository.createNewNote(
            title, getTimestamp(LocalDateTime.now(clock))
        ) != 0L
    }
}