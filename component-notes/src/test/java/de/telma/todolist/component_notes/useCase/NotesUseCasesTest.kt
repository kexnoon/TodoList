package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.assertTrue

class NotesUseCasesTest {

    private lateinit var notesRepository: NoteRepository

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
    }

    @Test
    fun `renames note if the new title is correct`() = runTest {
        val useCase = RenameNoteUseCase(notesRepository)
        val newTitle = "New Title"
        val expectedUpdatedNote = testNote.copy(title = newTitle)
        coEvery { notesRepository.updateNote(expectedUpdatedNote) } returns true

        val result = useCase(testNote, newTitle)

        assertTrue(result)
        coVerify(exactly = 1) { notesRepository.updateNote(expectedUpdatedNote) }
    }

    @Test
    fun `RenameNoteUseCase returns false if repository method returns false`() = runTest {
        val useCase = RenameNoteUseCase(notesRepository)
        val expectedUpdatedNote = testNote.copy(title = "")
        coEvery { notesRepository.updateNote(expectedUpdatedNote) } returns false

        val result = useCase(testNote, "")

        assertFalse(result)
        coVerify(exactly = 1) { notesRepository.updateNote(expectedUpdatedNote) }
    }

    @Test
    fun `updates note status if the new status is correct`() = runTest {
        val useCase = UpdateNoteStatusUseCase(notesRepository)
        val newStatus = NoteStatus.COMPLETE
        val expectedUpdatedNote = testNote.copy(status = newStatus)

        coEvery { notesRepository.updateNote(expectedUpdatedNote) } returns true
        val result = useCase(testNote, newStatus)

        assertTrue(result)
        coVerify(exactly = 1) { notesRepository.updateNote(expectedUpdatedNote) }
    }

    @Test
    fun `UpdateNoteStatusUseCase return false if repository method returns false`() = runTest {
        val useCase = UpdateNoteStatusUseCase(notesRepository)
        val newStatus = NoteStatus.COMPLETE
        val expectedUpdatedNote = testNote.copy(status = newStatus)
        coEvery { notesRepository.updateNote(expectedUpdatedNote) } returns false

        val result = useCase(testNote, newStatus)

        assertFalse(result)
        coVerify(exactly = 1) { notesRepository.updateNote(expectedUpdatedNote) }
    }

    private val testNote = Note(
        id = 1L,
        title = "Old Title",
        status = NoteStatus.IN_PROGRESS,
        tasksList = emptyList()
    )

}