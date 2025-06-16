package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNotes(): Flow<List<Note>>
}