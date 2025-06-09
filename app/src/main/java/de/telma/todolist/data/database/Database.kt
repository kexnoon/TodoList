package de.telma.todolist.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.telma.todolist.data.database.entity.NoteEntity
import de.telma.todolist.data.database.entity.NoteTaskEntity

@Database(entities = [
        NoteEntity::class,
        NoteTaskEntity::class
    ],
    version = 1
)
@TypeConverters(
    NoteStatusConverter::class,
    NoteTaskStatusConverter::class
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "todo_list_db"
    }

    abstract fun noteDao(): NoteDao
    abstract fun noteTaskDao(): NoteTaskDao
}