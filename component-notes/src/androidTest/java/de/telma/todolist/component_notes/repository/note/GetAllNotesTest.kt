package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
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
class GetAllNotesTest : BaseRepositoryTest() {
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun should_return_all_notes_when_notes_exist_in_db() = runTest {
        val tasks = listOf(getTask(), getTask())
        val note1 = getNote(tasksList = tasks)
        val note2 = getNote(id = 2L)

        database.noteDao().insertNote(note1.toNoteEntity())
        database.noteDao().insertNote(note2.toNoteEntity())
        note1.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note1.id))
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
    fun should_return_notes_with_no_tasks_when_such_notes_exist_in_db() = runTest {
        val note = getNote()

        database.noteDao().insertNote(note.toNoteEntity())

        val expected = listOf(note)
        val result = repository.getAllNotes().first()

        assertEquals(
            expected, result,
            "Repository doesn't return notes with no tasks. "
        )

    }

    @Test
    fun should_return_empty_list_when_no_notes_in_db() = runTest {
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