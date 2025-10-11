package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.utils.toNote
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(AndroidJUnit4::class)
class GetNotesByIdTest: BaseRepositoryTest() {
    lateinit var repository: NoteRepository

    private val testNote1 = Note(
        id = 999L,
        title = "testNote1",
        status = NoteStatus.IN_PROGRESS,
        tasksList = listOf(
            NoteTask(
                id = 3L,
                title = "task1",
                status = NoteTaskStatus.IN_PROGRESS
            ),
            NoteTask(
                id = 4L,
                title = "task2",
                status = NoteTaskStatus.COMPLETE
            )
        ),
        lastUpdatedTimestamp = "2022-12-13T14:15:16Z"
    )

    private val noteWithNoTasks = Note(
        id = 99L,
        title = "testNote2",
        status = NoteStatus.COMPLETE,
        tasksList = listOf(),
        lastUpdatedTimestamp = "2022-12-13T14:15:16Z"
    )


    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun getNoteById_returns_correct_note() = runTest {
        val note = testNote1

        database.noteDao().insertNote(note.toNoteEntity())
        note.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note.id))
        }

        val expected = note
        val result = repository.getNoteById(note.id).first()
        assertEquals(expected, result, "getNoteById returns wrong note by correct ID!")
    }

    @Test
    fun getNoteById_returns_the_same_note_as_in_DB() = runTest {
        val note = testNote1

        database.noteDao().insertNote(note.toNoteEntity())
        note.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note.id))
        }

        val expected = database.noteDao().getNoteWithTasksById(note.id).first()?.toNote()
        val result = repository.getNoteById(note.id).first()

        assertNotNull(expected, "getNoteById expected to return Note. Actual result: null!")

        assertEquals(expected, result, "Notes from getNoteById and from DB are not the same!")
    }

    @Test
    fun getNoteById_returns_empty_list_when_id_is_incorrect() = runTest {
        val note = testNote1

        database.noteDao().insertNote(note.toNoteEntity())
        note.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note.id))
        }
        val incorrectId = 99999L

        repository.getNoteById(incorrectId).test {
            val value = awaitItem()
            assertEquals(null, value, "getNoteById expected to return null when id is incorrect. ")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun getNoteById_returns_note_with_no_tasks() = runTest {
        val note = noteWithNoTasks

        database.noteDao().insertNote(note.toNoteEntity())
        val noteId = note.id

        repository.getNoteById(noteId).test {
            val value = awaitItem()
            assertNotNull(value, "getNoteById expected to return a note with no task. Actual result: null!")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    override fun teardown() {
        super.teardown()
    }

}