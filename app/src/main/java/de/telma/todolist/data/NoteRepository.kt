package de.telma.todolist.data

import de.telma.todolist.data.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNotes(): Flow<List<Note>>
}