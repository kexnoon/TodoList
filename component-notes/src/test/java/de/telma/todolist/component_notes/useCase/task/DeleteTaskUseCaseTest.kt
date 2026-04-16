package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.repository.FolderRepository
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

class DeleteTaskUseCaseTest: BaseNoteComponentUnitTest() {
    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var noteRepository: NoteRepositoryImpl
    private lateinit var folderRepository: FolderRepository

    @Before
    fun setUp() {
        noteRepository = mockk<NoteRepositoryImpl>()
        taskRepository = mockk<TaskRepositoryImpl>()
        folderRepository = mockk<FolderRepository>()
    }

    @Test
    fun `should return SUCCESS if task deletion successful and folder timestamp updated`() = runTest {
        val useCase = DeleteTaskUseCase(
            repository = taskRepository,
            noteRepository = noteRepository,
            folderRepository = folderRepository,
            clock = getClockForTest(getUpdatedTimestamp())
        )
        val noteId = 100L
        val note = getNote(id = noteId).copy(folderId = 7L)
        val testTask = getTaskInProgress(id = 101L, title = "Initial Task")
        val taskToDelete = testTask.copy(id = 1L)
        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { taskRepository.deleteTask(taskToDelete) } returns true
        coEvery { folderRepository.updateFolderTimestamp(7L, getUpdatedTimestamp()) } returns true

        val result = useCase(noteId, taskToDelete)

        assertEquals(
            expected = DeleteTaskUseCase.Result.SUCCESS,
            actual = result
        )
        coVerifyOrder {
            noteRepository.getNoteById(noteId)
            taskRepository.deleteTask(taskToDelete)
            folderRepository.updateFolderTimestamp(7L, getUpdatedTimestamp())
        }
    }

    @Test
    fun `should not update folder timestamp when note has no folder`() = runTest {
        val useCase = DeleteTaskUseCase(
            repository = taskRepository,
            noteRepository = noteRepository,
            folderRepository = folderRepository,
            clock = getClockForTest(getUpdatedTimestamp())
        )
        val noteId = 101L
        val note = getNote(id = noteId).copy(folderId = null)
        val taskToDelete = getTaskInProgress(id = 2L, title = "Task")

        coEvery { noteRepository.getNoteById(noteId) } returns flowOf(note)
        coEvery { taskRepository.deleteTask(taskToDelete) } returns true

        val result = useCase(noteId, taskToDelete)

        assertEquals(DeleteTaskUseCase.Result.SUCCESS, result)
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }
}
