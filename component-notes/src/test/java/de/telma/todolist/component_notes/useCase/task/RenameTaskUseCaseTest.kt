package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.utils.timestampFormat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RenameTaskUseCaseTest {
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl

    private val initialNoteTimestamp = "2023-01-01T10:00:00Z" // Example initial timestamp
    private val expectedUpdatedTimestamp = "2023-01-01T12:00:00Z" // Timestamp from our fixed clock
    private val testTask: NoteTask = NoteTask(id = 101L, title = "Initial Task", status = NoteTaskStatus.IN_PROGRESS)
    private val testNoteId = 1L
    private val testNote: Note = Note(
        id = testNoteId,
        title = "Base Note",
        status = NoteStatus.IN_PROGRESS,
        tasksList = listOf(testTask),
        lastUpdatedTimestamp = initialNoteTimestamp
    )
    private val fixedClock: Clock = getClockForTest(expectedUpdatedTimestamp)

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()
    }

    private fun getClockForTest(timestampString: String): Clock {
        val formatter = DateTimeFormatter.ofPattern(timestampFormat)
        val localDateTime = LocalDateTime.parse(timestampString, formatter)
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Clock.fixed(instant, ZoneOffset.UTC)
    }

    @Test
    fun `RenameTaskUseCase executes correctly if the new title is correct`() = runTest {
        // Arrange
        val useCase = RenameTaskUseCase(taskRepository, noteRepository, fixedClock)

        val newValidTitle = "Updated Task Title Successfully"
        val taskToSendForUpdate = testTask.copy(title = newValidTitle)
        val noteReturnedByRepo = testNote // The note that getNoteById will return
        val noteExpectedForFinalUpdate = noteReturnedByRepo.copy(lastUpdatedTimestamp = expectedUpdatedTimestamp)

        coEvery { taskRepository.updateTask(testNoteId, taskToSendForUpdate) } returns true
        coEvery { noteRepository.getNoteById(testNoteId) } returns flowOf(noteReturnedByRepo)
        coEvery { noteRepository.updateNote(noteExpectedForFinalUpdate) } returns true

        // Act
        val result = useCase(testNoteId, testTask, newValidTitle)

        // Assert
        assertTrue(result, "UseCase should return true on full success")
        coVerifyOrder {
            taskRepository.updateTask(testNoteId, taskToSendForUpdate)
            noteRepository.getNoteById(testNoteId)
            noteRepository.updateNote(noteExpectedForFinalUpdate)
        }
    }

    @Test
    fun `RenameTaskUseCase returns false if task update fails`() = runTest {
        // Arrange
        val useCase = RenameTaskUseCase(taskRepository, noteRepository, fixedClock)

        val newTitle = "Attempted Title Update"
        val taskToSendForUpdate = testTask.copy(title = newTitle)

        // Simulate the primary task update failing
        coEvery { taskRepository.updateTask(testNoteId, taskToSendForUpdate) } returns false

        // Act
        val result = useCase(testNoteId, testTask, newTitle)

        // Assert
        assertFalse(result, "UseCase should return false if task update fails")
        // Verify only the first repository call was made
        coVerify(exactly = 1) { taskRepository.updateTask(testNoteId, taskToSendForUpdate) }
        coVerify(exactly = 0) { noteRepository.getNoteById(any()) }
        coVerify(exactly = 0) { noteRepository.updateNote(any()) }
    }

}