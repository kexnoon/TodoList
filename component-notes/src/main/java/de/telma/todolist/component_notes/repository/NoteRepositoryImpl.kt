package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.toNote
import de.telma.todolist.component_notes.toNoteEntity
import de.telma.todolist.component_notes.toNotesList
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class NoteRepositoryImpl(private val database: AppDatabase): NoteRepository {

    override suspend fun getAllNotes(): Flow<List<Note>> {
        return database.noteDao()
            .getAllNotesWithTasks()
            .map(List<NoteWithTasks>::toNotesList)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getNoteById(id: Long): Flow<Note?> {
        return database.noteDao()
            .getNoteWithTasksById(noteId = id)
            .map { it?.toNote() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun createNewNote(title: String): Long = withContext(Dispatchers.IO) {
        val newNote = NoteEntity(
            id = 0,
            status = NoteStatus.IN_PROGRESS.toString(), //default status for new notes
            title = title
        )

        return@withContext database.noteDao().insertNote(newNote)
    }

    override suspend fun updateNote(note: Note): Boolean = withContext(Dispatchers.IO) {
        val entity = note.toNoteEntity()
        return@withContext database.noteDao().updateNote(entity) == 1
    }

    override suspend fun deleteNote(note: Note): Boolean = withContext(Dispatchers.IO) {
        val entity = note.toNoteEntity()
        return@withContext database.noteDao().deleteNote(entity) == 1
    }

}