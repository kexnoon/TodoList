package de.telma.todolist.storage.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteWithTasksById(noteId: Long): Flow<NoteWithTasks?>

    @Transaction
    @RawQuery(observedEntities = [NoteEntity::class])
    fun getNotesWithTasks(query: SupportSQLiteQuery): Flow<List<NoteWithTasks>>

    @Transaction
    @Query("SELECT * FROM notes WHERE folderId IS :folderId ORDER BY lastUpdatedTimestamp DESC")
    fun getNotesWithTasksInFolder(folderId: Long?): Flow<List<NoteWithTasks>>

    @Insert(onConflict = ABORT)
    suspend fun insertNote(entity: NoteEntity): Long

    @Update(onConflict = REPLACE)
    suspend fun updateNote(entity: NoteEntity): Int

    @Query("UPDATE notes SET lastUpdatedTimestamp = :timestamp WHERE id = :noteId")
    suspend fun updateNoteTimestamp(noteId: Long, timestamp: String): Int

    @Query("UPDATE notes SET folderId = :folderId WHERE id IN (:noteIds)")
    suspend fun updateNotesFolder(noteIds: List<Long>, folderId: Long?): Int

    @Delete
    suspend fun deleteNote(entity: NoteEntity): Int
}
