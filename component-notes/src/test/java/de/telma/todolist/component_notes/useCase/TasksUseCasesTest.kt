package de.telma.todolist.component_notes.useCase

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

class TasksUseCasesTest {

    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl

    private lateinit var fixedClock: Clock

    private val testNoteId = 1L
    private val initialNoteTimestamp = "2023-01-01T10:00:00Z" // Example initial timestamp
    private val expectedUpdatedTimestamp = "2023-01-01T12:00:00Z" // Timestamp from our fixed clock

    private lateinit var testTask: NoteTask
    private lateinit var testNote: Note

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()

        fixedClock = getClockForTest(expectedUpdatedTimestamp)

        testTask = NoteTask(id = 101L, title = "Initial Task", status = NoteTaskStatus.IN_PROGRESS)
        testNote = Note(
            id = testNoteId,
            title = "Base Note",
            status = NoteStatus.IN_PROGRESS,
            tasksList = listOf(testTask),
            lastUpdatedTimestamp = initialNoteTimestamp
        )
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

    @Test
    fun `updates task status if the new status is correct`() = runTest {
        val useCase = UpdateTaskStatusUseCase(taskRepository, noteRepository, fixedClock)
        // Arrange
        val newStatus = NoteTaskStatus.COMPLETE
        val taskToSendForUpdate = testTask.copy(status = newStatus)

        val noteReturnedByRepo = testNote // The note that getNoteById will return
        val noteExpectedForFinalUpdate = noteReturnedByRepo.copy(lastUpdatedTimestamp = expectedUpdatedTimestamp)

        // Mock behavior for successful operations
        coEvery { taskRepository.updateTask(testNoteId, taskToSendForUpdate) } returns true
        // Crucially, getNoteById must return a non-null Note for the 'let' block to execute
        coEvery { noteRepository.getNoteById(testNoteId) } returns flowOf(noteReturnedByRepo)
        coEvery { noteRepository.updateNote(noteExpectedForFinalUpdate) } returns true

        // Act
        val result = useCase(testNoteId, testTask, newStatus)

        // Assert
        assertTrue(result, "UseCase should return true on full success")

        // Verify that all repository methods were called in the correct order with the correct arguments
        coVerifyOrder {
            taskRepository.updateTask(testNoteId, taskToSendForUpdate)
            noteRepository.getNoteById(testNoteId) // Must yield a non-null Note for 'let'
            noteRepository.updateNote(noteExpectedForFinalUpdate)
        }
    }

    @Test
    fun `UpdateTaskStatusUseCase executes correctly if the new status is correct`() = runTest {
        // Arrange
        val useCase = UpdateTaskStatusUseCase(taskRepository, noteRepository, fixedClock)
        val newStatus = NoteTaskStatus.COMPLETE
        val taskToSendForUpdate = testTask.copy(status = newStatus)

        // This is the note object we expect to be passed to noteRepository.updateNote
        val noteExpectedForFinalUpdate = testNote.copy(lastUpdatedTimestamp = expectedUpdatedTimestamp)

        coEvery { taskRepository.updateTask(testNote.id, taskToSendForUpdate) } returns true
        coEvery { noteRepository.getNoteById(testNote.id) } returns flowOf(testNote) // Return the note before its timestamp update
        coEvery { noteRepository.updateNote(noteExpectedForFinalUpdate) } returns true

        // Act
        val result = useCase(testNote.id, testTask, newStatus)

        // Assert
        assertTrue(result, "UseCase should return true on full success")
        coVerifyOrder {
            taskRepository.updateTask(testNote.id, taskToSendForUpdate)
            noteRepository.getNoteById(testNote.id)
            noteRepository.updateNote(noteExpectedForFinalUpdate)
        }
    }

    @Test
    fun `UpdateTaskStatusUseCase return false if repository method returns false`() = runTest {
        val useCase = UpdateTaskStatusUseCase(taskRepository, noteRepository, fixedClock )
        val status = NoteTaskStatus.COMPLETE
        val expectedTask = testTask.copy(status = status)
        coEvery { taskRepository.updateTask(testNoteId, expectedTask) } returns false

        val result = useCase(noteId = testNoteId, task = expectedTask, newStatus = NoteTaskStatus.COMPLETE)

        assertFalse(result)
        coVerify(exactly = 1) { taskRepository.updateTask(testNoteId, expectedTask) }
    }

    @Test
    fun `DeleteTaskUseCase returns true if task is deleted`() = runTest {
        val useCase = DeleteTaskUseCase(taskRepository)
        val taskToDelete = testTask.copy(id = 1L)
        coEvery { taskRepository.deleteTask(taskToDelete) } returns true

        val result = useCase(taskToDelete)

        assertTrue(result, "DeleteTaskUseCase should return true if task is deleted, but returned false")
        coVerify(exactly = 1) { taskRepository.deleteTask(taskToDelete) }
    }


    private fun getClockForTest(timestampString: String): Clock {
        val formatter = DateTimeFormatter.ofPattern(timestampFormat)
        val localDateTime = LocalDateTime.parse(timestampString, formatter)
        val instant = localDateTime.toInstant(ZoneOffset.UTC)
        return Clock.fixed(instant, ZoneOffset.UTC)
    }

}