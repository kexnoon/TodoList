package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.timestampFormat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.assertTrue

class RenameNoteUseCaseTest {
    private lateinit var notesRepository: NoteRepository
    private val defaultTimestamp = "2023-01-01T10:00:00Z"
    private val updatedTimestamp = "2023-01-01T12:30:00Z"
    private val baseTestNote = Note(
        id = 1L,
        title = "Old Title",
        status = NoteStatus.IN_PROGRESS,
        tasksList = emptyList(),
        lastUpdatedTimestamp = defaultTimestamp
    )

    @Before
    fun setUp() {
        notesRepository = mockk<NoteRepository>()
    }

    private fun getClockForTest(timestampString: String): Clock {
        val formatter = DateTimeFormatter.ofPattern(timestampFormat)
        val localDateTime = LocalDateTime.parse(timestampString, formatter)
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Clock.fixed(instant, ZoneOffset.UTC)
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
}