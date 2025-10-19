package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class UpdateTaskUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var taskRepository: TaskRepository
    private lateinit var noteRepository: NoteRepository

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepository>()
        taskRepository = mockk<TaskRepository>()
    }

    @Test
    fun `should return SUCCESS if task update succeed`() = runTest {
        val useCase = UpdateTaskStatusUseCase(taskRepository, noteRepository, getClockForTest(getUpdatedTimestamp()))
        // SETUP
        val initialTask = getTaskInProgress()
        val newStatus = NoteTaskStatus.COMPLETE
        val updatedTask = initialTask.copy(status = newStatus)

        val testNoteId = 1L
        val noteFromRepo = getNote(id = testNoteId, tasks = listOf(updatedTask))
        val noteExpectedForFinalUpdate = noteFromRepo.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.updateTask(testNoteId, updatedTask) } returns true
        coEvery { noteRepository.getNoteById(testNoteId) } returns flowOf(noteFromRepo)
        coEvery { noteRepository.updateNote(noteExpectedForFinalUpdate) } returns true

        // ACT
        val result = useCase(testNoteId, initialTask, newStatus)

        // ASSERT
        assertEquals(
            expected = UpdateTaskStatusUseCase.Result.SUCCESS,
            actual = result
        )
        coVerifyOrder {
            taskRepository.updateTask(testNoteId, updatedTask)
            noteRepository.getNoteById(testNoteId)
            noteRepository.updateNote(noteExpectedForFinalUpdate)
        }
    }

    @Test
    fun `should return FAILURE if task update succeed but note update fails`() = runTest {
        val useCase = UpdateTaskStatusUseCase(taskRepository, noteRepository, getClockForTest(getUpdatedTimestamp()))

        // SETUP
        val initialTask = getTaskInProgress()
        val newStatus = NoteTaskStatus.COMPLETE
        val updatedTask = initialTask.copy(status = newStatus)

        val testNoteId = 1L
        val noteFromRepo = getNote(id = testNoteId, tasks = listOf(updatedTask))
        val noteExpectedForFinalUpdate = noteFromRepo.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.updateTask(testNoteId, updatedTask) } returns true
        coEvery { noteRepository.getNoteById(testNoteId) } returns flowOf(noteFromRepo)
        coEvery { noteRepository.updateNote(noteExpectedForFinalUpdate) } returns false // Note update fails

        // ACT
        val result = useCase(testNoteId, initialTask, newStatus)

        // ASSERT
        assertEquals(
            expected = UpdateTaskStatusUseCase.Result.FAILURE,
            actual = result
        )
        coVerifyOrder {
            taskRepository.updateTask(testNoteId, updatedTask)
            noteRepository.getNoteById(testNoteId)
            noteRepository.updateNote(noteExpectedForFinalUpdate)
        }
    }
}