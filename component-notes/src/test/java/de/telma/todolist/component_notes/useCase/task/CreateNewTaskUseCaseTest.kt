package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.useCase.BaseNoteComponentUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class CreateNewTaskUseCaseTest : BaseNoteComponentUnitTest() {

    private lateinit var taskRepository: TaskRepository
    private lateinit var noteRepository: NoteRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var useCase: CreateNewTaskUseCase

    @Before
    fun setUp() {
        taskRepository = mockk()
        noteRepository = mockk()
        folderRepository = mockk()
        useCase = CreateNewTaskUseCase(
            taskRepository = taskRepository,
            noteRepository = noteRepository,
            folderRepository = folderRepository,
            clock = getClockForTest(getUpdatedTimestamp())
        )
    }

    @Test
    fun `should return SUCCESS when task creation and note update succeed`() = runTest {
        val note = getNote(id = 9L).copy(folderId = null)
        val updatedNote = note.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.createNewTask(note.id, "New task") } returns true
        coEvery { noteRepository.updateNote(updatedNote) } returns true

        val result = useCase(note, "New task")

        assertEquals(CreateNewTaskUseCase.Result.SUCCESS(note.id), result)
        coVerifyOrder {
            taskRepository.createNewTask(note.id, "New task")
            noteRepository.updateNote(updatedNote)
        }
    }

    @Test
    fun `should update folder timestamp when note has folder and task creation succeeds`() = runTest {
        val note = getNote(id = 10L).copy(folderId = 9L)
        val updatedNote = note.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.createNewTask(note.id, "New task") } returns true
        coEvery { noteRepository.updateNote(updatedNote) } returns true
        coEvery { folderRepository.updateFolderTimestamp(9L, getUpdatedTimestamp()) } returns true

        val result = useCase(note, "New task")

        assertEquals(CreateNewTaskUseCase.Result.SUCCESS(note.id), result)
        coVerifyOrder {
            taskRepository.createNewTask(note.id, "New task")
            noteRepository.updateNote(updatedNote)
            folderRepository.updateFolderTimestamp(9L, getUpdatedTimestamp())
        }
    }

    @Test
    fun `should not update folder timestamp when note has no folder`() = runTest {
        val note = getNote(id = 11L).copy(folderId = null)
        val updatedNote = note.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.createNewTask(note.id, "New task") } returns true
        coEvery { noteRepository.updateNote(updatedNote) } returns true

        val result = useCase(note, "New task")

        assertEquals(CreateNewTaskUseCase.Result.SUCCESS(note.id), result)
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }

    @Test
    fun `should return FAILURE and skip note update when task creation fails`() = runTest {
        val note = getNote(id = 12L).copy(folderId = 9L)

        coEvery { taskRepository.createNewTask(note.id, "New task") } returns false

        val result = useCase(note, "New task")

        assertEquals(CreateNewTaskUseCase.Result.FAILURE, result)
        coVerify(exactly = 1) { taskRepository.createNewTask(note.id, "New task") }
        coVerify(exactly = 0) { noteRepository.updateNote(any()) }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }

    @Test
    fun `should return FAILURE when note update fails after successful task creation`() = runTest {
        val note = getNote(id = 13L).copy(folderId = 9L)
        val updatedNote = note.copy(lastUpdatedTimestamp = getUpdatedTimestamp())

        coEvery { taskRepository.createNewTask(note.id, "New task") } returns true
        coEvery { noteRepository.updateNote(updatedNote) } returns false

        val result = useCase(note, "New task")

        assertEquals(CreateNewTaskUseCase.Result.FAILURE, result)
        coVerifyOrder {
            taskRepository.createNewTask(note.id, "New task")
            noteRepository.updateNote(updatedNote)
        }
        coVerify(exactly = 0) { folderRepository.updateFolderTimestamp(any(), any()) }
    }
}
