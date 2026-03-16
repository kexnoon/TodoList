package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val repository: NoteRepository
) {
    operator suspend fun invoke(search: SearchModel?): Flow<List<Note>> {
        return repository.getNotes(search)
    }
}
