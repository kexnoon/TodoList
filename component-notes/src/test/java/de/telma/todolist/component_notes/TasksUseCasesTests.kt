package de.telma.todolist.component_notes

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.useCase.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.UpdateNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateTaskStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TasksUseCasesTests {

    private lateinit var tasksRepository: TaskRepository

    @Before
    fun setUp() {
        tasksRepository = mockk<TaskRepository>()
    }

    @Test
    fun `renames task if the new title is correct`() = runTest {
        val useCase = RenameTaskUseCase(tasksRepository)
        val newTitle = "New Title"
        val expectedUpdatedTask = testTask.copy(title = newTitle)
        coEvery { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) } returns true

        val result = useCase(noteId = testNoteId, task = testTask, newTitle = newTitle)

        assertTrue(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) }
    }

    @Test
    fun `rename task returns false if repository method returns false`() = runTest {
        val useCase = RenameTaskUseCase(tasksRepository)
        val newTitle = "New Title"
        val expectedTask = testTask.copy(title = newTitle)
        coEvery { tasksRepository.updateTask(testNoteId, expectedTask) } returns false

        val result = useCase(noteId = testNoteId, task = expectedTask, newTitle = newTitle)

        assertFalse(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedTask) }
    }

    @Test
    fun `updates task status if the new status is correct`() = runTest {
        val useCase = UpdateTaskStatusUseCase(tasksRepository)
        val newStatus = NoteTaskStatus.COMPLETE
        val expectedUpdatedTask = testTask.copy(status = newStatus)
        coEvery { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) } returns true

        val result = useCase(noteId = testNoteId, task = testTask, newStatus = newStatus)

        assertTrue(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) }
    }

    @Test
    fun `update note status return false if repository method returns false`() = runTest {
        val useCase = UpdateTaskStatusUseCase(tasksRepository)
        val status = NoteTaskStatus.COMPLETE
        val expectedTask = testTask.copy(status = status)
        coEvery { tasksRepository.updateTask(testNoteId, expectedTask) } returns false

        val result = useCase(noteId = testNoteId, task = expectedTask, newStatus = NoteTaskStatus.COMPLETE)

        assertFalse(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedTask) }
    }

    private val testTask = NoteTask(
        id = 0L,
        title = "NoteTask",
        status = NoteTaskStatus.IN_PROGRESS
    )

    private val testNoteId = 0L
}