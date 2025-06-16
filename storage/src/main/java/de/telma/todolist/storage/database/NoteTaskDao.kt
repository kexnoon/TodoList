package de.telma.todolist.storage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.Update
import de.telma.todolist.storage.database.entity.NoteTaskEntity

@Dao
interface NoteTaskDao {
    @Insert(onConflict = ABORT)
    suspend fun insertTask(entity: NoteTaskEntity)

    @Update
    suspend fun updateTask(entity: NoteTaskEntity)

    @Delete
    suspend fun deleteTask(entity: NoteTaskEntity)
}