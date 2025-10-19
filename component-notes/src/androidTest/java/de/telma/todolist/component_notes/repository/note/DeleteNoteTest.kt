package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.utils.toNoteEntity
import de.telma.todolist.component_notes.utils.toNoteTaskEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DeleteNoteTest: BaseRepositoryTest() {
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun `should_return_true_when_note_is_deleted_successfully`() = runTest {
        val tasks = listOf(getTask(), getTask())
        val note = getNote(id = 1L, tasksList = tasks)

        database.noteDao().insertNote(note.toNoteEntity())

        note.tasksList.forEach {
            database.noteTaskDao().insertTask(it.toNoteTaskEntity(note.id))
        }

        val result = repository.deleteNote(note)

        assertTrue(result, "deleteNote returned false with correct note")
    }

    @Test
    fun `should_return_false_when_note_to_be_deleted_does_not_exist`() = runTest {
        val tasks = listOf(getTask(), getTask())
        val note = getNote(id = 1L, tasksList = tasks)
        val result = repository.deleteNote(note)

        assertFalse(result, "deleteNote returned true with non-existing note")
    }

    @After
    override fun teardown() {
        super.teardown()
    }
}