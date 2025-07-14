package de.telma.todolist.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import de.telma.todolist.storage.BuildConfig
import de.telma.todolist.storage.database.entity.NoteEntity
import de.telma.todolist.storage.database.entity.NoteTaskEntity

@Database(entities = [
        NoteEntity::class,
        NoteTaskEntity::class
    ],
    version = 6
)

abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = BuildConfig.DB_NAME
    }

    abstract fun noteDao(): NoteDao
    abstract fun noteTaskDao(): NoteTaskDao
}