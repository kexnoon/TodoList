package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository

class DeleteNoteUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(note: Note): Boolean {
        return noteRepository.deleteNote(note)
    }
}