package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import kotlin.test.assertEquals
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

    @Test
    fun `SyncNoteStatusUseCase sets note status to COMPLETE when all tasks are complete`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)
        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.COMPLETE)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)
        val note = testNote.copy(tasksList = listOf(task1, task2))

        val updatedNote = note.copy(status = NoteStatus.COMPLETE)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        val result = useCase(note)

        assertEquals(result, SyncNoteStatusUseCase.SyncStatus.SYNC_SUCCEED, "SyncNoteStatusUseCase status should be SYNC_SUCCEED, but was $result")
    }

    @Test
    fun `SyncNoteStatusUseCase sets note status to IN_PROGRESS when not all tasks are complete`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)
        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.IN_PROGRESS)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)
        val note = testNote.copy(status = NoteStatus.COMPLETE, tasksList = listOf(task1, task2))

        val updatedNote = note.copy(status = NoteStatus.IN_PROGRESS)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        val result = useCase(note)

        assertEquals(result, SyncNoteStatusUseCase.SyncStatus.SYNC_SUCCEED, "SyncNoteStatusUseCase status should be SYNC_SUCCEED, but was $result")
    }

    @Test
    fun `SyncNoteStatusUseCase returns UP_TO_DATE if note status is already synced`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)
        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.IN_PROGRESS)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)
        val note = testNote.copy(tasksList = listOf(task1, task2))

        val result = useCase(note)

        assertEquals(result, SyncNoteStatusUseCase.SyncStatus.UP_TO_DATE, "SyncNoteStatusUseCase status should be UP_TO_DATE, but was $result")
    }

    @Test
    fun `SyncNoteStatusUseCase returns SYNC_FAILED if note status update failed`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)
        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.COMPLETE)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)
        val note = testNote.copy(tasksList = listOf(task1, task2))

        val updatedNote = note.copy(status = NoteStatus.COMPLETE)
        coEvery { notesRepository.updateNote(updatedNote) } returns false

        val result = useCase(note)

        assertEquals(result, SyncNoteStatusUseCase.SyncStatus.SYNC_FAILED, "SyncNoteStatusUseCase status should be SYNC_FAILED, but was $result")
    }


    private val testNote = Note(
        id = 1L,
        title = "Old Title",
        status = NoteStatus.IN_PROGRESS,
        tasksList = emptyList()
    )

}