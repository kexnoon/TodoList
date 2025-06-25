package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note

class RenameNoteUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: Note, newTitle: String): Boolean {
        val updatedNote = note.copy(title = newTitle)
        return repository.updateNote(updatedNote)
    }

}