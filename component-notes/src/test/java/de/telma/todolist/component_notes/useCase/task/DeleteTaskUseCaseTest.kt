package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class DeleteTaskUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()
    }

    @Test
    fun `should return SUCCESS if task deletion successful`() = runTest {
        val useCase = DeleteTaskUseCase(taskRepository)
        val testTask = getTaskInProgress(id = 101L, title = "Initial Task")
        val taskToDelete = testTask.copy(id = 1L)
        coEvery { taskRepository.deleteTask(taskToDelete) } returns true

        val result = useCase(taskToDelete)

        assertEquals(
            expected = DeleteTaskUseCase.Result.SUCCESS,
            actual = result
        )
        coVerify(exactly = 1) { taskRepository.deleteTask(taskToDelete) }
    }
}