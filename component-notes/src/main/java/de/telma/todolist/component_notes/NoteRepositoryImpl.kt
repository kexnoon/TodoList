package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

internal class NoteRepositoryImpl(private val database: AppDatabase): NoteRepository {
    override suspend fun getNotes(): Flow<List<Note>> {
        delay(1000L)
        return database.noteDao()
            .getAllNotesWithTasks()
            .map(List<NoteWithTasks>::toNotesList)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }
}