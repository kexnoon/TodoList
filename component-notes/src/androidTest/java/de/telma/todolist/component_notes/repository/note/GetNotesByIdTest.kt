package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
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
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun should_return_correct_note_when_id_is_correct() = runTest {
        val note = getNote()

        database.noteDao().insertNote(note.toNoteEntity())

        val expected = note
        val result = repository.getNoteById(note.id).first()
        assertEquals(expected, result, "getNoteById returns wrong note by correct ID!")
    }

    @Test
    fun should_return_same_note_as_from_db_when_id_is_correct() = runTest {
        val note = getNote(tasksList = listOf(getTask(), getTask()))

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
    fun should_return_null_when_id_is_incorrect() = runTest {
        val note = getNote(tasksList = listOf(getTask(), getTask()))

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
    fun should_return_note_when_it_has_no_tasks() = runTest {
        val note = getNote(tasksList = listOf())

        database.noteDao().insertNote(note.toNoteEntity())
        val noteId = note.id

        repository.getNoteById(noteId).test {
            val value = awaitItem()
            assertNotNull(value, "getNoteById expected to return a note with no task. Actual result: null!")
            assertEquals(note, value, "The returned note with no tasks did not match the expected note.")
            cancelAndIgnoreRemainingEvents()
        }
    }

    @After
    override fun teardown() {
        super.teardown()
    }

}
