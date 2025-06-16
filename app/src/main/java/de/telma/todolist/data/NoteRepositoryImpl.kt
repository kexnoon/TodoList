package de.telma.todolist.data

import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteWithTasks
import de.telma.todolist.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(private val database: AppDatabase): NoteRepository {
    override suspend fun getNotes(): Flow<List<Note>> {
        delay(1000L)
        return database.noteDao()
            .getAllNotesWithTasks()
            .map(List<NoteWithTasks>::toNotesList)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }
}