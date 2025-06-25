package de.telma.todolist.storage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteWithTasksById(noteId: Long): Flow<NoteWithTasks>

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTasks(): Flow<List<NoteWithTasks>>

    @Insert(onConflict = ABORT)
    suspend fun insertNote(entity: NoteEntity): Long

    @Update(onConflict = REPLACE)
    suspend fun updateNote(entity: NoteEntity): Int

    @Delete
    suspend fun deleteNote(entity: NoteEntity): Int
}