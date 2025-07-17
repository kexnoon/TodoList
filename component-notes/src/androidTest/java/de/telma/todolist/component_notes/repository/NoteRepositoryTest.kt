package de.telma.todolist.component_notes.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.toNote
import de.telma.todolist.component_notes.toNoteEntity
import de.telma.todolist.component_notes.toNoteTaskEntity
import de.telma.todolist.storage.database.entity.NoteWithTasks
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class NoteRepositoryTest: BaseRepositoryTest() {

    lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun getAllNotes_return_same_notes_as_in_DB() = runTest {
        val note1 = testNote1
        val note2 = testNote2

        database.noteDao().insertNote(note1.toNoteEntity())
        database.noteDao().insertNote(note2.toNoteEntity())
        note1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note1.id))
        }
        note2.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note2.id))
        }

        val expected = database.noteDao()
            .getAllNotesWithTasks()
            .first()
            .map(NoteWithTasks::toNote)
        val actual = repository.getAllNotes().first()

        assertTrue(expected.isNotEmpty(), "Notes list from DAO is empty!")
        assertTrue(actual.isNotEmpty(), "Notes list from repository is empty!")
        assertEquals(expected.size, actual.size, "Notes lists should be the same size!")
        assertEquals(expected, actual, "Notes content should match! ")
    }

    @Test
    fun getAllNotes_successfully_returns_note_with_no_tasks() = runTest {
        val note = noteWithNoTasks

        database.noteDao().insertNote(note.toNoteEntity())

        val expected = listOf(note)
        val result = repository.getAllNotes().first()

        assertEquals(expected, result,
            "Repository doesn't return notes with no tasks. "
        )

    }

    @Test
    fun getAllNotes_successfully_returns_empty_list_when_no_notes_in_db() = runTest {
        val notesFromRepository = repository.getAllNotes().first()
        assertNotNull(
            notesFromRepository,
            "Repository doesn't return empty list when there are no notes in DB."
        )
        assertTrue(
            notesFromRepository.isEmpty(),
            "Repository doesn't return empty list when there are no notes in DB."
        )
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

    @Test
    fun createNewNote_successfully_creates_new_note() = runTest {
        val expectedTitle = "new note"

        val newNoteID = repository.createNewNote(expectedTitle)
        assertTrue(newNoteID == 0L, "createNewNote returned a note with wrong ID! New Note's ID: $newNoteID")

        val newNote = database.noteDao().getNoteWithTasksById(newNoteID).first()
        assertNotNull(newNote, "Failed to retrieve a note created via createNewNote!")

        val result = newNote.note.title
        assertEquals(expectedTitle, result, "createNewNote: incorrectly passed title!. ")

    }

    @Test
    fun updateNote_correctly_executes_when_note_passed() = runTest {
        val note = testNote1
        database.noteDao().insertNote(note.toNoteEntity())
        val updatedNote = note.copy(title = "updated_note")

        val result = repository.updateNote(updatedNote)

        assertTrue(result, "updateNote returned false or other response with correct note")
    }

    @Test
    fun updateNote_returns_false_when_non_existing_note_updated() = runTest {
        val note = testNote1

        val result = repository.updateNote(note)

        assertFalse(result, "updateNote returned true or other response with non-existing note")
    }

    @Test
    fun deleteNote_correctly_deletes_existing_note() = runTest {
        val note = testNote1
        database.noteDao().insertNote(note.toNoteEntity())
        note.tasksList.forEach { task ->
            database.noteTaskDao().insertTask(task.toNoteTaskEntity(note.id))
        }

        val result = repository.deleteNote(note)
        val tasksAfterNoteDeleted = database.noteTaskDao().getAllTasksByNoteId(note.id)

        assertTrue(result, "deleteNote haven't delete existing note despite correct note was passed")
        assertTrue(tasksAfterNoteDeleted.isEmpty(), "deleteNote correctly deleted note, however associated tasks were not deleted")
    }

    @Test
    fun deleteNote_returns_false_on_non_existing_notes() = runTest {
        val note = testNote1

        val result = repository.deleteNote(note)

        assertFalse(result, "deleteNote returned true while trying to delete non-existing note")
    }


    @After
    override fun teardown() {
        super.teardown()
    }

    private val testNote1 = Note(
        id = 0L,
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
        )
    )

    private val testNote2 = Note(
        id = 1L,
        title = "testNote2",
        status = NoteStatus.COMPLETE,
        tasksList = listOf(
            NoteTask(
                id = 5L,
                title = "task3",
                status = NoteTaskStatus.COMPLETE
            )
        )
    )

    private val noteWithNoTasks = Note(
        id = 1L,
        title = "testNote2",
        status = NoteStatus.COMPLETE,
        tasksList = listOf()
    )

}