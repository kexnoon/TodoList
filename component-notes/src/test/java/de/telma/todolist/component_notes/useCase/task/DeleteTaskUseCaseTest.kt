package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class DeleteTaskUseCaseTest {
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl
    private val testTask: NoteTask = NoteTask(id = 101L, title = "Initial Task", status = NoteTaskStatus.IN_PROGRESS)

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()
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
}