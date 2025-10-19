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

open class BaseRepositoryTest : KoinTest {

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

    protected fun getDefaultTimestamp(): String = "2022-12-13T14:15:16Z"

    protected fun getUpdatedTimestamp(): String = "2023-11-12T13:14:15Z"

    protected fun getTask(
        id: Long = (1L..9999L).random(),
        title: String = "Test Task ${id}",
        status: NoteTaskStatus = NoteTaskStatus.IN_PROGRESS
    ) = NoteTask(
        id = id,
        title = title,
        status = status
    )

    protected fun getNote(
        id: Long = (1L..9999L).random(),
        title: String = "Test Note ${id}",
        status: NoteStatus = NoteStatus.IN_PROGRESS,
        tasksList: List<NoteTask> = listOf(),
        lastUpdatedTimestamp: String = getDefaultTimestamp()
    ) = Note(
        id = id,
        title = title,
        status = status,
        tasksList = tasksList,
        lastUpdatedTimestamp = lastUpdatedTimestamp
    )
}