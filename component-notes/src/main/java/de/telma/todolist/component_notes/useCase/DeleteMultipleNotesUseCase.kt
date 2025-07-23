package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository

class DeleteMultipleNotesUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(notesToDelete: List<Note>): Boolean {
        notesToDelete.forEach {
            if (!repository.deleteNote(it))
                return false
        }
        return true
    }

}