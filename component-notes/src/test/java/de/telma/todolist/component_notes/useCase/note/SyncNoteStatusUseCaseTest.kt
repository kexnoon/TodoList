package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class SyncNoteStatusUseCaseTest {
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
    fun `SyncNoteStatusUseCase sets note status to COMPLETE when all tasks are complete`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)
        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.COMPLETE)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)
        val note = testNote.copy(tasksList = listOf(task1, task2))
        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)

        val updatedNote = note.copy(status = NoteStatus.COMPLETE)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        val result = useCase(note.id)

        assertEquals(
            result, SyncNoteStatusUseCase.SyncStatus.SYNC_SUCCEED,
            "SyncNoteStatusUseCase status should be SYNC_SUCCEED, but was $result"
        )
    }

    @Test
    fun `SyncNoteStatusUseCase sets note status to IN_PROGRESS when not all tasks are complete`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)

        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.IN_PROGRESS)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)

        val note = testNote.copy(status = NoteStatus.COMPLETE, tasksList = listOf(task1, task2))
        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)
        val updatedNote = note.copy(status = NoteStatus.IN_PROGRESS)

        coEvery { notesRepository.updateNote(updatedNote) } returns true

        val result = useCase(note.id)

        assertEquals(
            result, SyncNoteStatusUseCase.SyncStatus.SYNC_SUCCEED,
            "SyncNoteStatusUseCase status should be SYNC_SUCCEED, but was $result"
        )
    }

    @Test
    fun `SyncNoteStatusUseCase returns UP_TO_DATE if note status is already synced`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)

        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.IN_PROGRESS)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)

        val note = testNote.copy(tasksList = listOf(task1, task2))
        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)

        val result = useCase(note.id)

        assertEquals(
            expected = result, actual = SyncNoteStatusUseCase.SyncStatus.UP_TO_DATE,
            "SyncNoteStatusUseCase status should be UP_TO_DATE, but was $result"
        )
    }

    @Test
    fun `SyncNoteStatusUseCase returns SYNC_FAILED if note status update failed`() = runTest {
        val useCase = SyncNoteStatusUseCase(notesRepository)

        val task1 = NoteTask(id = 2L, title = "Task1", status = NoteTaskStatus.COMPLETE)
        val task2 = NoteTask(id = 3L, title = "Task2", status = NoteTaskStatus.COMPLETE)

        val note = testNote.copy(tasksList = listOf(task1, task2))
        coEvery { notesRepository.getNoteById(note.id) } returns flowOf(note)
        val updatedNote = note.copy(status = NoteStatus.COMPLETE)

        coEvery { notesRepository.updateNote(updatedNote) } returns false

        val result = useCase(note.id)

        assertEquals(
            expected = result, actual = SyncNoteStatusUseCase.SyncStatus.SYNC_FAILED,
            "SyncNoteStatusUseCase status should be SYNC_FAILED, but was $result"
        )
    }
}