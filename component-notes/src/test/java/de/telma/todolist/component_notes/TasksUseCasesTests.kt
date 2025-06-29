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
    private lateinit var testTask: NoteTask
    private val testNoteId = 0L

    @Before
    fun setUp() {
        tasksRepository = mockk<TaskRepository>()
        testTask = NoteTask(
            id = 0L,
            title = "NoteTask",
            status = NoteTaskStatus.IN_PROGRESS
        )
    }

    @Test
    fun RenameTaskUseCase_correctName_returnsTrue() = runTest {
        val useCase = RenameTaskUseCase(tasksRepository)
        val newTitle = "New Title"
        val expectedUpdatedTask = testTask.copy(title = newTitle)
        coEvery { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) } returns true

        val result = useCase(noteId = testNoteId, task = testTask, newTitle = newTitle)

        assertTrue(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) }
    }

    @Test
    fun RenameTaskUseCase_somethingWrong_returnsFalse() = runTest {
        val useCase = RenameTaskUseCase(tasksRepository)
        val newTitle = "New Title"
        val expectedTask = testTask.copy(title = newTitle)
        coEvery { tasksRepository.updateTask(testNoteId, expectedTask) } returns false

        val result = useCase(noteId = testNoteId, task = expectedTask, newTitle = newTitle)

        assertFalse(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedTask) }
    }

    @Test
    fun UpdateTaskStatusUseCase_correctName_returnsTrue() = runTest {
        val useCase = UpdateTaskStatusUseCase(tasksRepository)
        val newStatus = NoteTaskStatus.COMPLETE
        val expectedUpdatedTask = testTask.copy(status = newStatus)
        coEvery { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) } returns true

        val result = useCase(noteId = testNoteId, task = testTask, newStatus = newStatus)

        assertTrue(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedUpdatedTask) }
    }

    @Test
    fun UpdateTaskStatusUseCasee_somethingWrong_returnsFalse() = runTest {
        val useCase = UpdateTaskStatusUseCase(tasksRepository)
        val status = NoteTaskStatus.COMPLETE
        val expectedTask = testTask.copy(status = status)
        coEvery { tasksRepository.updateTask(testNoteId, expectedTask) } returns false

        val result = useCase(noteId = testNoteId, task = expectedTask, newStatus = NoteTaskStatus.COMPLETE)

        assertFalse(result)
        coVerify(exactly = 1) { tasksRepository.updateTask(testNoteId, expectedTask) }    }


}