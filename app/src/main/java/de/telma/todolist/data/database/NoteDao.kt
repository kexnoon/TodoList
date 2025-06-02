package de.telma.todolist.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import de.telma.todolist.data.database.entity.NoteWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteWithTasksById(noteId: Long): Flow<NoteWithTasks?>

    @Transaction
    @Query("SELECT * FROM notes")
    fun getAllNotesWithTasks(): Flow<List<NoteWithTasks>>
}