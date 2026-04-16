package de.telma.todolist.component_notes.repository

import de.telma.todolist.component_notes.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {

    fun getAll(): Flow<List<Folder>>

    suspend fun createFolder(name: String, timestamp: String): Long

    suspend fun renameFolder(id: Long, name: String, timestamp: String): Boolean

    suspend fun deleteFolder(id: Long): Boolean

    suspend fun updateFolderTimestamp(id: Long, timestamp: String): Boolean

    suspend fun updateFolderTimestamp(folderIds: List<Long>, timestamp: String): Boolean
}

