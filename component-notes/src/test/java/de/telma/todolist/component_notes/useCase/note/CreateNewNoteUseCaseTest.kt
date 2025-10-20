package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import kotlin.test.assertEquals

class CreateNewNoteUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var clock: Clock
    private lateinit var useCase: CreateNewNoteUseCase

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepository>()
        clock = getClockForTest(getUpdatedTimestamp())
        useCase = CreateNewNoteUseCase(noteRepository, clock)
    }

    @Test
    fun `should return SUCCESS when note creation is successful`() = runTest {
        // SETUP
        val title = "New Note Title"
        val expectedNoteId = 1L
        coEvery { noteRepository.createNewNote(any(), any()) } returns expectedNoteId

        // ACT
        val result = useCase(title)

        // ASSERT
        assertEquals(
            expected = CreateNewNoteUseCase.Result.SUCCESS(expectedNoteId),
            actual = result
        )
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp()) }
    }

    @Test
    fun `should return FAILURE when note creation throws an exception`() = runTest {
        // SETUP
        val title = "New Note Title"
        val exception = RuntimeException("Database error")
        coEvery { noteRepository.createNewNote(any(), any()) } throws exception

        // ACT
        val result = useCase(title)

        // ASSERT
        assertEquals(
            expected = CreateNewNoteUseCase.Result.FAILURE,
            actual = result
        )
        coVerify { noteRepository.createNewNote(title, getUpdatedTimestamp()) }
    }
}