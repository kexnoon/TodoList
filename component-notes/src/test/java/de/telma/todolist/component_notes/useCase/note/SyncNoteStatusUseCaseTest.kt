package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SyncNoteStatusUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var notesRepository: NoteRepository
    private lateinit var useCase: SyncNoteStatusUseCase

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
        useCase = SyncNoteStatusUseCase(notesRepository)
    }

    @Test
    fun `should set note status to COMPLETE after all tasks are complete`() = runTest {
        // SETUP
        val task1 = getTaskComplete()
        val task2 = getTaskComplete()

        val note = getNote(tasks = listOf(task1, task2))
        val updatedNote = note.copy(status = NoteStatus.COMPLETE)

        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        // ACT
        val result = useCase(note.id)

        // ASSERT
        assertEquals(
            expected = SyncNoteStatusUseCase.Result.SyncSucceed,
            actual = result
        )
    }

    @Test
    fun `should set note status to IN_PROGRESS when tasks are incomplete`() = runTest {
        // SETUP
        val task1 = getTaskInProgress()
        val task2 = getTaskComplete()

        val note = getNote(tasks = listOf(task1, task2), status = NoteStatus.COMPLETE)
        val updatedNote = note.copy(status = NoteStatus.IN_PROGRESS)

        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        // ACT
        val result = useCase(note.id)

        // ASSERT
        assertEquals(
            expected = SyncNoteStatusUseCase.Result.SyncSucceed,
            actual = result
        )
    }

    @Test
    fun `should return UP_TO_DATE if no updates in note`() = runTest {
        // SETUP
        val task1 = getTaskInProgress()
        val task2 = getTaskComplete()

        val note = getNote(tasks = listOf(task1, task2))
        val updatedNote = note.copy()

        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(updatedNote)

        // ACT
        val result = useCase(note.id)

        // ASSERT
        assertEquals(
            expected = SyncNoteStatusUseCase.Result.UpToDate,
            actual = result
        )
    }

    @Test
    fun `should return SYNC_FAILED if note status update failed`() = runTest {
        // SETUP
        val task1 = getTaskComplete()
        val task2 = getTaskComplete()

        val note = getNote(tasks = listOf(task1, task2))
        val updatedNote = note.copy(status = NoteStatus.COMPLETE)

        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)
        coEvery { notesRepository.updateNote(updatedNote) } returns false

        // ACT
        val result = useCase(note.id)

        // ASSERT
        assertEquals(
            expected = SyncNoteStatusUseCase.Result.SyncFailed,
            actual = result,
        )
    }
}