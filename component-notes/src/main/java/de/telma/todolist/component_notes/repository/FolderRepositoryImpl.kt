package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.utils.toFoldersList
import de.telma.todolist.storage.database.AppDatabase
import de.telma.todolist.storage.database.entity.FolderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class FolderRepositoryImpl(
    private val database: AppDatabase
) : FolderRepository {

    override fun getAll(): Flow<List<Folder>> {
        return database.folderDao()
            .getAll()
            .map { it.toFoldersList() }
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun createFolder(name: String, timestamp: String): Long = withContext(Dispatchers.IO) {
        database.folderDao().insert(
            FolderEntity(
                id = 0L,
                name = name,
                lastUpdatedTimestamp = timestamp
            )
        )
    }

    override suspend fun renameFolder(id: Long, name: String, timestamp: String): Boolean = withContext(Dispatchers.IO) {
        database.folderDao().renameById(id, name, timestamp) == 1
    }

    override suspend fun deleteFolder(id: Long): Boolean = withContext(Dispatchers.IO) {
        database.folderDao().deleteById(id) == 1
    }

    override suspend fun updateFolderTimestamp(id: Long, timestamp: String): Boolean = withContext(Dispatchers.IO) {
        database.folderDao().updateFolderTimestampById(id, timestamp) == 1
    }
}
