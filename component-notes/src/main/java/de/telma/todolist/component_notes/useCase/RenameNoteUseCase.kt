package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository

class RenameNoteUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: Note, newTitle: String): Boolean {
        val updatedNote = note.copy(title = newTitle)
        return repository.updateNote(updatedNote)
    }

}