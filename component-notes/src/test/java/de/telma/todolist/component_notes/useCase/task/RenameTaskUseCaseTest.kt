package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RenameTaskUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()
    }

    @Test
    fun `should return SUCCESS if the new title is correct`() = runTest {
        // Arrange
        val useCase = RenameTaskUseCase(
            taskRepository,
            noteRepository,
            getClockForTest(getUpdatedTimestamp())
        )

        val newTitle = "Updated Title"
        val testTask = getTaskInProgress(title = "Initial Title")
        val updatedTask = testTask.copy(title = newTitle)

        val testNoteId = 1L
        val testNote = getNote(id = testNoteId, tasks = listOf(updatedTask))
        val updatedNote = testNote.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        // Mock repository calls
        coEvery { taskRepository.updateTask(testNoteId, updatedTask) } returns true
        coEvery { noteRepository.getNoteById(testNoteId) } returns flowOf(testNote)
        coEvery { noteRepository.updateNote(updatedNote) } returns true

        // Act
        val result = useCase(testNoteId, testTask, newTitle)

        // Assert
        assertEquals(RenameTaskUseCase.Result.SUCCESS, result)

        // Verify all repository calls were made in the correct order
        coVerifyOrder {
            taskRepository.updateTask(testNoteId, updatedTask)
            noteRepository.getNoteById(testNoteId)
            noteRepository.updateNote(updatedNote)
        }
    }

    @Test
    fun `should return FAILURE if task update fails`() = runTest {
        // Arrange
        val useCase = RenameTaskUseCase(taskRepository, noteRepository, getClockForTest())

        val newTitle = "Attempted Title Update"
        val testTask = getTaskInProgress(title = newTitle)
        val testNoteId = 1L

        // Simulate the primary task update failing
        coEvery { taskRepository.updateTask(testNoteId, testTask) } returns false

        // Act
        val result = useCase(testNoteId, testTask, newTitle)

        // Assert
        assertEquals(
            expected = RenameTaskUseCase.Result.FAILURE,
            actual = result
        )
        // Verify only the first repository call was made
        coVerify(exactly = 1) { taskRepository.updateTask(testNoteId, testTask) }
        coVerify(exactly = 0) { noteRepository.getNoteById(any()) }
        coVerify(exactly = 0) { noteRepository.updateNote(any()) }
    }

}