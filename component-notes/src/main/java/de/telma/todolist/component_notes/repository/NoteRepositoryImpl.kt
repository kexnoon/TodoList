package de.telma.todolist.component_notes.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.utils.SqlHelper
import de.telma.todolist.component_notes.utils.toNote
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNotesList
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class NoteRepositoryImpl(private val database: AppDatabase): NoteRepository {

    override suspend fun getNotes(search: SearchModel?): Flow<List<Note>> {
        val queryModel = SqlHelper().getNotesQueryModel(search ?: SearchModel())
        val query = SimpleSQLiteQuery(queryModel.query, queryModel.args.toTypedArray())

        return database.noteDao()
            .getNotesWithTasks(query)
            .map(List<NoteWithTasks>::toNotesList)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getNotesInFolder(folderId: Long?): Flow<List<Note>> {
        return database.noteDao()
            .getNotesWithTasksInFolder(folderId)
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

    override suspend fun createNewNote(
        title: String,
        timestamp: String,
        folderId: Long?
    ): Long = withContext(Dispatchers.IO) {
        val newNote = NoteEntity(
            id = 0,
            status = NoteStatus.IN_PROGRESS.statusValue, //default status for new notes
            title = title,
            createdTimestamp = timestamp,
            lastUpdatedTimestamp = timestamp,
            folderId = folderId
        )

        val id = database.noteDao().insertNote(newNote)

        return@withContext id
    }

    override suspend fun updateNoteTimestamp(noteId: Long, timestamp: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext database.noteDao().updateNoteTimestamp(noteId, timestamp) == 1
    }

    override suspend fun updateNotesFolder(noteIds: List<Long>, folderId: Long?): Boolean = withContext(Dispatchers.IO) {
        if (noteIds.isEmpty()) return@withContext true
        return@withContext database.noteDao().updateNotesFolder(noteIds, folderId) == noteIds.size
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
