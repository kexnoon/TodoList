package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UpdateNoteStatusUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var notesRepository: NoteRepository
    private lateinit var useCase: UpdateNoteStatusUseCase

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
        val clock = getClockForTest(getUpdatedTimestamp())
        useCase = UpdateNoteStatusUseCase(notesRepository, clock)
    }

    @Test
    fun `should update note and return SUCCESS when given new status`() = runTest {
        // SETUP
        val newStatus = NoteStatus.COMPLETE
        val defaultNote = getNote()
        val updatedNote = defaultNote.copy(status = newStatus, lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { notesRepository.updateNote(updatedNote) } returns true

        // ACT
        val result = useCase(defaultNote, newStatus)

        // ASSERT
        assertEquals(
            expected = UpdateNoteStatusUseCase.Result.Success,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.updateNote(updatedNote) }
    }

    @Test
    fun `should fail when repository method fails`() = runTest {
        // SETUP
        val updatedStatus = NoteStatus.COMPLETE
        val defaultNote = getNote()
        val updatedNote = defaultNote.copy(status = updatedStatus, lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { notesRepository.updateNote(updatedNote) } returns false

        // ACT
        val result = useCase(defaultNote, updatedStatus)

        // ASSERT
        assertEquals(
            expected = UpdateNoteStatusUseCase.Result.Failure,
            actual = result
        )
        coVerify(exactly = 1) { notesRepository.updateNote(updatedNote) }
    }
}