package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.utils.toNoteEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class UpdateNoteTest: BaseRepositoryTest() {
    lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    private val updateTimestamp1 = "2022-12-13T14:15:16Z"
    private val updateTimestamp2 = "2023-11-12T13:14:15Z"

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

    @Test
    fun updateNote_correctly_executes_when_note_passed() = runTest {
        val note = testNote1.copy(lastUpdatedTimestamp = updateTimestamp1)
        database.noteDao().insertNote(note.toNoteEntity())

        val expectedTimestamp = updateTimestamp2
        val updatedNote = note.copy(title = "updated_note", lastUpdatedTimestamp = expectedTimestamp)

        val result = repository.updateNote(updatedNote)
        assertTrue(result, "updateNote returned false or other response with correct note")

        val updatedNoteFromDb = repository.getNoteById(note.id)
        val actualTimestamp = updatedNoteFromDb.first()?.lastUpdatedTimestamp
        assertEquals(expectedTimestamp, actualTimestamp,
            "updateNote: can't retrieve updated timestamp!."
        )
    }

    @Test
    fun updateNote_returns_false_when_non_existing_note_updated() = runTest {
        val note = testNote1

        val result = repository.updateNote(note)

        assertFalse(result, "updateNote returned true or other response with non-existing note")
    }


    @After
    override fun teardown() {
        super.teardown()
    }
}