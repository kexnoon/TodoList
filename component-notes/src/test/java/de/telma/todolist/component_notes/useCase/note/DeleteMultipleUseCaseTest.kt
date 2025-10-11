package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.assertTrue

class DeleteMultipleUseCaseTest {
    private lateinit var notesRepository: NoteRepository
    private val defaultTimestamp = "2023-01-01T10:00:00Z"
    private val testNote = Note(
        id = 1L,
        title = "Original Title",
        status = NoteStatus.IN_PROGRESS,
        tasksList = emptyList(),
        lastUpdatedTimestamp = defaultTimestamp
    )

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
    }

    @Test
    fun `DeleteMultipleNotesUseCase returns true if all notes are deleted`() = runTest {
        val useCase = DeleteMultipleNotesUseCase(notesRepository)
        val note1 = testNote.copy(id = 1L)
        val note2 = testNote.copy(id = 2L)
        val notesToDelete = listOf(note1, note2)

        coEvery { notesRepository.deleteNote(note1) } returns true
        coEvery { notesRepository.deleteNote(note2) } returns true

        val result = useCase(notesToDelete)

        assertTrue(
            actual = result,
            "DeleteMultipleNotesUseCase should return true if all notes are deleted, but returned false."
        )
        coVerify(exactly = 1) { notesRepository.deleteNote(note1) }
        coVerify(exactly = 1) { notesRepository.deleteNote(note2) }
    }

    @Test
    fun `DeleteMultipleNotesUseCase returns false if not all notes are deleted`() = runTest {
        val useCase = DeleteMultipleNotesUseCase(notesRepository)
        val note1 = testNote.copy(id = 1L)
        val note2 = testNote.copy(id = 2L)

        val notesToDelete = listOf(note1, note2)

        coEvery { notesRepository.deleteNote(note1) } returns true
        coEvery { notesRepository.deleteNote(note2) } returns false

        val result = useCase(notesToDelete)

        assertFalse(
            "DeleteMultipleNotesUseCase should return false if not all notes are deleted",
            result
        )
        coVerify(exactly = 1) { notesRepository.deleteNote(note1) }
        coVerify(exactly = 1) { notesRepository.deleteNote(note2) }
    }

    @Test
    fun `DeleteNoteUseCase returns true if note is deleted`() = runTest {
        val useCase = DeleteNoteUseCase(notesRepository)
        val noteToDelete = testNote.copy(id = 1L)
        coEvery { notesRepository.deleteNote(noteToDelete) } returns true
        val result = useCase(noteToDelete)

        assertTrue(
            "DeleteNoteUseCase should return true if all notes are deleted, but returned false",
            result
        )
        coVerify(exactly = 1) { notesRepository.deleteNote(noteToDelete) }
    }
}