package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    /**
     * Used on MainScreen to show the list of all notes
     */
    suspend fun getAllNotes(): Flow<List<Note>>

    /**
     * Used on NoteScreen to show the selected not
     */
    suspend fun getNoteById(id: Long): Flow<Note?>

    /**
     * Returns Note's ID so we could show it on the next screen
     */
    suspend fun createNewNote(title: String, timestamp: String): Long

    suspend fun updateNote(note: Note): Boolean

    suspend fun deleteNote(note: Note): Boolean
}