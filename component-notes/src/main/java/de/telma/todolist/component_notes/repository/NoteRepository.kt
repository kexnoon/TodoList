package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    /**
     * Will be used on MainScreen to show the list of all notes
     */
    suspend fun getAllNotes(): Flow<List<Note>>

    /**
     * Will be used on NoteOverviewScreen to show the selected not
     */
    suspend fun getNoteById(id: Long): Flow<Note>

    /**
     * Will return Note's ID so we could show it on the next screen
     */
    suspend fun createNewNote(title: String): Long

    suspend fun updateNote(note: Note): Boolean

    suspend fun deleteNote(note: Note): Boolean
}