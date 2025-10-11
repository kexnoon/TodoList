package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class RenameNoteUseCase(
    private val repository: NoteRepository,

    private val clock: Clock
) {

    suspend operator fun invoke(note: Note, newTitle: String): Boolean {
        val timestamp = getTimestamp(LocalDateTime.now(clock))
        val updatedNote = note.copy(title = newTitle, lastUpdatedTimestamp = timestamp)
        return repository.updateNote(updatedNote)
    }

}