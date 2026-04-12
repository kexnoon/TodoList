package de.telma.todolist.storage.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Query
import de.telma.todolist.storage.database.entity.FolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao {

    @Query("SELECT * FROM folders ORDER BY lastUpdatedTimestamp DESC, name DESC")
    fun getAll(): Flow<List<FolderEntity>>

    @Insert(onConflict = ABORT)
    suspend fun insert(entity: FolderEntity): Long

    @Query("UPDATE folders SET name = :name, lastUpdatedTimestamp = :timestamp WHERE id = :id")
    suspend fun renameById(id: Long, name: String, timestamp: String): Int

    @Query("UPDATE folders SET lastUpdatedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateFolderTimestampById(id: Long, timestamp: String): Int

    @Query("DELETE FROM folders WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}
