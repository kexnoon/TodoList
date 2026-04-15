package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.SearchModel
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun getNotes(search: SearchModel? = null, folderId: Long? = null): Flow<List<Note>>
    suspend fun getNotesInFolder(folderId: Long?): Flow<List<Note>>

    /**
     * Used on NoteScreen to show the selected not
     */
    suspend fun getNoteById(id: Long): Flow<Note?>

    /**
     * Returns Note's ID so we could show it on the next screen
     */
    suspend fun createNewNote(title: String, timestamp: String, folderId: Long? = null): Long

    suspend fun updateNoteTimestamp(noteId: Long, timestamp: String): Boolean
    suspend fun updateNotesFolder(noteIds: List<Long>, folderId: Long?): Boolean

    // Legacy method kept for compatibility with existing use cases.
    suspend fun updateNote(note: Note): Boolean

    suspend fun deleteNote(note: Note): Boolean
}
