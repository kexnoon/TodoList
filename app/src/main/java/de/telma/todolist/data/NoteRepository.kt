package de.telma.todolist.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getMessage(): Flow<String>
}