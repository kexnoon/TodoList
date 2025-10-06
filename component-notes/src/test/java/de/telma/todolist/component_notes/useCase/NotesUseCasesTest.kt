package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.timestampFormat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlinx.coroutines.test.runTest
import org.junit.Before
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotesUseCasesTest {

    private lateinit var notesRepository: NoteRepository
    private lateinit var baseTestNote: Note

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
        baseTestNote = Note(
            id = 1L,
            title = "Original Title",
            status = NoteStatus.IN_PROGRESS,
            tasksList = emptyList(),
            lastUpdatedTimestamp = defaultTimestamp
        )
    }


    @Test
    fun `renameNoteUseCase - given valid new title - then updates note and returns true`() = runTest {
        val clock = getClockForTest(updatedTimestamp)
        val useCase = RenameNoteUseCase(notesRepository, clock)
        val newTitle = "Successfully Updated Title"

        val expectedNoteAfterUpdate = baseTestNote.copy(
            title = newTitle,
            lastUpdatedTimestamp = updatedTimestamp
        )
        coEvery { notesRepository.updateNote(expectedNoteAfterUpdate) } returns true

        // Act
        val result = useCase(baseTestNote, newTitle)

        // Assert
        assertTrue(result, "Expected use case to return true on successful update")
        coVerify(exactly = 1) { notesRepository.updateNote(expectedNoteAfterUpdate) }
    }

    @Test
    fun `renameNoteUseCase - when repository fails to update - then returns false`() = runTest {
        val clock = getClockForTest(updatedTimestamp)
        val useCase = RenameNoteUseCase(notesRepository, clock)
        val newTitleToAttempt = "Failed Update Title"

        val noteThatWouldBeSentToRepo = baseTestNote.copy(
            title = newTitleToAttempt,
            lastUpdatedTimestamp = updatedTimestamp
        )
        coEvery { notesRepository.updateNote(noteThatWouldBeSentToRepo) } returns false

        val result = useCase(baseTestNote, newTitleToAttempt)

        assertFalse("Expected use case to return false when repository update fails", result)
        coVerify(exactly = 1) { notesRepository.updateNote(noteThatWouldBeSentToRepo) }
    }

    @Test
    fun `updateNoteStatusUseCase - given valid new status - then updates note and returns true`() = runTest {
        val clock = getClockForTest(updatedTimestamp)
        val useCase = UpdateNoteStatusUseCase(notesRepository, clock)
        val newStatus = NoteStatus.COMPLETE

        val expectedNoteAfterUpdate = baseTestNote.copy(
            status = newStatus,
            lastUpdatedTimestamp = updatedTimestamp
        )
        coEvery { notesRepository.updateNote(expectedNoteAfterUpdate) } returns true

        val result = useCase(baseTestNote, newStatus)

        assertTrue(result, "Expected use case to return true on successful status update")
        coVerify(exactly = 1) { notesRepository.updateNote(expectedNoteAfterUpdate) }
    }

    @Test
    fun `updateNoteStatusUseCase - when repository fails to update - then returns false`() = runTest {
        val clock = getClockForTest(updatedTimestamp)
        val useCase = UpdateNoteStatusUseCase(notesRepository, clock)
        val newStatusToAttempt = NoteStatus.COMPLETE

        val noteThatWouldBeSentToRepo = baseTestNote.copy(
            status = newStatusToAttempt,
            lastUpdatedTimestamp = updatedTimestamp
        )
        coEvery { notesRepository.updateNote(noteThatWouldBeSentToRepo) } returns false

        val result = useCase(baseTestNote, newStatusToAttempt)

        assertFalse("Expected use case to return false when repository status update fails", result)
        coVerify(exactly = 1) { notesRepository.updateNote(noteThatWouldBeSentToRepo) }
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

        val updatedNote = note.copy(status = NoteStatus.IN_PROGRESS)
        coEvery { notesRepository.updateNote(updatedNote) } returns true

        val result = useCase(note)

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

        val result = useCase(note)

        assertEquals(
            result, SyncNoteStatusUseCase.SyncStatus.UP_TO_DATE,
            "SyncNoteStatusUseCase status should be UP_TO_DATE, but was $result"
        )
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

        assertEquals(
            result, SyncNoteStatusUseCase.SyncStatus.SYNC_FAILED,
            "SyncNoteStatusUseCase status should be SYNC_FAILED, but was $result"
        )
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
            result,
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

    private fun getClockForTest(timestampString: String): Clock {
        val formatter = DateTimeFormatter.ofPattern(timestampFormat)
        val localDateTime = LocalDateTime.parse(timestampString, formatter)
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Clock.fixed(instant, ZoneOffset.UTC)
    }
    private val defaultTimestamp = "2023-01-01T10:00:00Z"
    private val updatedTimestamp = "2023-01-01T12:30:00Z"


    private val testNote = Note(
        id = 1L,
        title = "Old Title",
        status = NoteStatus.IN_PROGRESS,
        tasksList = emptyList(),
        lastUpdatedTimestamp = defaultTimestamp
    )

}