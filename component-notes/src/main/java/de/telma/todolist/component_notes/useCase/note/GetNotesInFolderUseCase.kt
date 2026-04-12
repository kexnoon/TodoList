package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesInFolderUseCase(
    private val repository: NoteRepository
) {
    sealed interface Result {
        data object SUCCESS: Result
        data object FAILURE: Result
    }

    suspend operator fun invoke(folderId: Long?): Flow<List<Note>> {
        return repository.getNotesInFolder(folderId)
    }
}

