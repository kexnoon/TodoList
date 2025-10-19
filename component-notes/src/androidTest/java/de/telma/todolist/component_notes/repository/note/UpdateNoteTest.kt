package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
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
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun should_return_true_when_note_is_updated_successfully() = runTest {
        val note = getNote(lastUpdatedTimestamp = getDefaultTimestamp())
        database.noteDao().insertNote(note.toNoteEntity())

        val expectedTimestamp = getUpdatedTimestamp()
        val updatedNote = note.copy(title = "updated_note", lastUpdatedTimestamp = expectedTimestamp)

        val result = repository.updateNote(updatedNote)
        assertTrue(result, "updateNote returned false with correct note")

        val updatedNoteFromDb = repository.getNoteById(note.id)
        val actualTimestamp = updatedNoteFromDb.first()?.lastUpdatedTimestamp
        assertEquals(expectedTimestamp, actualTimestamp,
            "updateNote: can't retrieve updated timestamp!."
        )
    }

    @Test
    fun should_return_false_when_note_to_be_updated_does_not_exist() = runTest {
        val note = getNote()

        val result = repository.updateNote(note)

        assertFalse(result, "updateNote returned true with non-existing note")
    }


    @After
    override fun teardown() {
        super.teardown()
    }
}