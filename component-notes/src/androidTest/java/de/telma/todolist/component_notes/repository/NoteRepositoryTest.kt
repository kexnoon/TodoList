package de.telma.todolist.component_notes.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
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
        database.noteDao().insertNote(testNote1.toNoteEntity())
        database.noteDao().insertNote(testNote2.toNoteEntity())
        testNote1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(testNote1.id))
        }
        testNote2.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(testNote2.id))
        }

        val expected = database.noteDao()
            .getAllNotesWithTasks()
            .first()
            .map(NoteWithTasks::toNote)
        val result = repository.getAllNotes().first()

        assertTrue(expected.isNotEmpty(), "Notes list from DAO is empty!")
        assertTrue(result.isNotEmpty(), "Notes list from repository is empty!")
        assertEquals(
            expected.size, result.size,
            "Notes lists should be the same size! " +
                    "Expected: ${expected.size}, Result: ${result.size}"
        )
        assertEquals(
            expected, result,
            "Notes content should match! " +
                    "Expected: $expected, Result: $result"
        )
    }

    @Test
    fun getAllNotes_successfully_returns_note_with_no_tasks() = runTest {
        database.noteDao().insertNote(noteWithNoTasks.toNoteEntity())

        val expected = listOf(noteWithNoTasks)
        val result = repository.getAllNotes().first()

        assertEquals(expected, result,
            "Repository doesn't return notes with no tasks. " +
                    "Actual result: non-empty list!"
        )

    }

    @Test
    fun getAllNotes_successfully_returns_empty_list_when_no_tasks_in_db() = runTest {
        val notesFromRepository = repository.getAllNotes().first()
        assertNotNull(
            notesFromRepository,
            "Repository doesn't return empty list when there are no notes in DB. Actual return: null!"
        )
        assertTrue(
            notesFromRepository.isEmpty(),
            "Repository doesn't return empty list when there are no notes in DB. Actual return: non-empty list!"
        )
    }

    @Test
    fun getNoteById_returns_correct_note() = runTest {
        database.noteDao().insertNote(testNote1.toNoteEntity())
        testNote1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(testNote1.id))
        }

        val expected = testNote1
        val result = repository.getNoteById(testNote1.id).first()
        assertEquals(expected, result,
            "getNoteById returns wrong note by correct ID!" +
                "Expected note: $testNote1 , Actual result: $result" )
    }

    @Test
    fun getNoteById_returns_the_same_note_as_in_DB() = runTest {
        database.noteDao().insertNote(testNote1.toNoteEntity())
        testNote1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(testNote1.id))
        }

        val expected = database.noteDao().getNoteWithTasksById(testNote1.id)
            .first()
            .toNote()

        val result = repository.getNoteById(testNote1.id).first()

        assertEquals(
            expected, result,
            "Notes from getNoteById and from DB are not the same!" +
                "Expected: $expected, Result: $result")
    }

    @Test
    fun getNoteById_returns_empty_list_when_id_is_incorrect() = runTest {
        /* database.noteDao().insertNote(testNote1.toNoteEntity())
        testNote1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(testNote1.id))
        }
        val incorrectId = 999999L

        val result = repository.getNoteById(incorrectId)

        assertTrue { result != null } */

    }

    @Test
    fun getNoteById_returns_note_with_no_tasks() = runTest {}


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