package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
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
class GetAllNotesTest: BaseRepositoryTest() {
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

    private val testNote2 = Note(
        id = 99L,
        title = "testNote2",
        status = NoteStatus.COMPLETE,
        tasksList = listOf(
            NoteTask(
                id = 5L,
                title = "task3",
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

    @After
    override fun teardown() {
        super.teardown()
    }
}