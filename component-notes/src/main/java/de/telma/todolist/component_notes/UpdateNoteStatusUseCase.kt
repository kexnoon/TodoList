package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus

class UpdateNoteStatusUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: Note, newStatus: NoteStatus): Boolean {
        val updatedNote = note.copy(status = newStatus)
        return repository.updateNote(updatedNote)
    }

}