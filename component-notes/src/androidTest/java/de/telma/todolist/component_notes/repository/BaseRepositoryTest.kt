package de.telma.todolist.component_notes.repository

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.storage.database.AppDatabase
import org.junit.After
import org.junit.Before
import org.koin.test.KoinTest

open class BaseRepositoryTest(): KoinTest {

    protected lateinit var database: AppDatabase

    @Before
    open fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    open fun teardown() {
        database.close()
    }

}