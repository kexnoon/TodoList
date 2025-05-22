package de.telma.todolist.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOn

class NoteRepositoryImpl: NoteRepository {
    override fun getMessage(): Flow<String> =
        flow {
            delay(300L)
            emit("Hello World!")
        }.flowOn(Dispatchers.IO)
}