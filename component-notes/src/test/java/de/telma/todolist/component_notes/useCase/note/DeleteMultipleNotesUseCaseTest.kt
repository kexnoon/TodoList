package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DeleteMultipleNotesUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var notesRepository: NoteRepository
    private lateinit var useCase: DeleteNoteUseCase

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
        useCase = DeleteNoteUseCase(notesRepository)
    }

    @Test
    fun `should succeed if all notes are deleted`() = runTest {
        // SETUP
        val note1 = getNote()
        val note2 = getNote()

        val notesToDelete = listOf(note1, note2)

        coEvery { notesRepository.deleteNote(note1) } returns true
        coEvery { notesRepository.deleteNote(note2) } returns true

        // ACT
        val result = useCase(notesToDelete)

        // ASSERT
        assertEquals(
            expected = DeleteNoteUseCase.Result.SUCCESS,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.deleteNote(note1) }
        coVerify(exactly = 1) { notesRepository.deleteNote(note2) }
    }

    @Test
    fun `should fail if not all notes are deleted`() = runTest {
        // SETUP
        val useCase = DeleteNoteUseCase(notesRepository)
        val note1 = getNote()
        val note2 = getNote()

        val notesToDelete = listOf(note1, note2)

        coEvery { notesRepository.deleteNote(note1) } returns true
        coEvery { notesRepository.deleteNote(note2) } returns false

        // ACT
        val result = useCase(notesToDelete)

        // ASSERT
        assertEquals(
            expected = DeleteNoteUseCase.Result.FAILURE,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.deleteNote(note1) }
        coVerify(exactly = 1) { notesRepository.deleteNote(note2) }
    }
}