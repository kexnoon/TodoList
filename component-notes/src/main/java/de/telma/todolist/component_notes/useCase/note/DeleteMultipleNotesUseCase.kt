package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository

class DeleteMultipleNotesUseCase(private val repository: NoteRepository) {

    sealed interface Result {
        object SUCCESS : Result
        object FAILURE : Result
    }

    suspend operator fun invoke(notesToDelete: List<Note>): Result {
        notesToDelete.forEach {
            if (!repository.deleteNote(it))
                return Result.FAILURE
        }
        return Result.SUCCESS
    }

}