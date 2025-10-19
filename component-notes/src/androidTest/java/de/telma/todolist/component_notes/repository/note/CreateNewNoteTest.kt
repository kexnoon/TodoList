package de.telma.todolist.component_notes.repository.note

import androidx.test.ext.junit.runners.AndroidJUnit4
import de.telma.todolist.component_notes.repository.BaseRepositoryTest
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
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
class CreateNewNoteTest: BaseRepositoryTest() {
    private lateinit var repository: NoteRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = NoteRepositoryImpl(database)
    }

    @Test
    fun should_return_new_note_id_when_note_is_created_successfully() = runTest {
        val expectedTitle = "new note"
        val creationTimestamp = getDefaultTimestamp()

        val newNoteID = repository.createNewNote(expectedTitle, creationTimestamp)
        assertTrue(newNoteID == 1L, "createNewNote returned a note with wrong ID! New Note's ID: $newNoteID")

        val newNote = database.noteDao().getNoteWithTasksById(newNoteID).first()
        assertNotNull(newNote, "Failed to retrieve a note created via createNewNote!")

        val newNoteTitle = newNote.note.title
        assertEquals(expectedTitle, newNoteTitle, "createNewNote: incorrectly passed title!.")

        val newNoteTimestamp = newNote.note.lastUpdatedTimestamp
        assertEquals(
            creationTimestamp, newNoteTimestamp,
            "createNewNote: can't retrieve creation timestamp!."
        )
    }


    @After
    override fun teardown() {
        super.teardown()
    }

}
