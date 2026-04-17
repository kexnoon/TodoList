package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.GetFoldersUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.RenameNoteUseCase
import de.telma.todolist.component_notes.useCase.note.SetNoteFolderUseCase
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.task.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.task.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.task.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.task.UpdateTaskStatusUseCase
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.UiState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.assertIs
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
abstract class NoteScreenViewModelTestBase {

    protected val testDispatcher = StandardTestDispatcher()

    protected lateinit var coordinator: NavigationCoordinator
    protected lateinit var noteRepository: NoteRepository
    protected lateinit var getFoldersUseCase: GetFoldersUseCase
    protected lateinit var createFolderUseCase: CreateFolderUseCase
    protected lateinit var setNoteFolderUseCase: SetNoteFolderUseCase
    protected lateinit var renameNoteUseCase: RenameNoteUseCase
    protected lateinit var deleteNoteUseCase: DeleteNoteUseCase
    protected lateinit var createNewTaskUseCase: CreateNewTaskUseCase
    protected lateinit var renameTaskUseCase: RenameTaskUseCase
    protected lateinit var updateTaskStatusUseCase: UpdateTaskStatusUseCase
    protected lateinit var deleteTaskUseCase: DeleteTaskUseCase
    protected lateinit var syncNoteStatusUseCase: SyncNoteStatusUseCase

    protected lateinit var viewModel: NoteScreenViewModel

    @Before
    fun setUpBase() {
        Dispatchers.setMain(testDispatcher)

        coordinator = mockk(relaxed = true)
        noteRepository = mockk()
        getFoldersUseCase = mockk()
        createFolderUseCase = mockk()
        setNoteFolderUseCase = mockk()
        renameNoteUseCase = mockk()
        deleteNoteUseCase = mockk()
        createNewTaskUseCase = mockk()
        renameTaskUseCase = mockk()
        updateTaskStatusUseCase = mockk()
        deleteTaskUseCase = mockk()
        syncNoteStatusUseCase = mockk()

        coEvery { noteRepository.getNoteById(NOTE_ID) } returns flowOf(baseNote)
        every { getFoldersUseCase() } returns flowOf(emptyList())
        coEvery { createFolderUseCase(any()) } returns CreateFolderUseCase.Result.FAILURE
        coEvery { setNoteFolderUseCase(any(), any()) } returns SetNoteFolderUseCase.Result.FAILURE
        coEvery { renameNoteUseCase(any(), any()) } returns RenameNoteUseCase.Result.FAILURE
        coEvery { deleteNoteUseCase(any<Note>()) } returns false
        coEvery { createNewTaskUseCase(any(), any()) } returns CreateNewTaskUseCase.Result.FAILURE
        coEvery { renameTaskUseCase(any(), any(), any()) } returns RenameTaskUseCase.Result.FAILURE
        coEvery { updateTaskStatusUseCase(any(), any(), any()) } returns UpdateTaskStatusUseCase.Result.FAILURE
        coEvery { deleteTaskUseCase(any(), any()) } returns DeleteTaskUseCase.Result.FAILURE
        coEvery { syncNoteStatusUseCase(any()) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDownBase() {
        Dispatchers.resetMain()
    }

    protected fun createViewModel(): NoteScreenViewModel {
        return NoteScreenViewModel(
            noteId = NOTE_ID,
            coordinator = coordinator,
            noteRepository = noteRepository,
            getFoldersUseCase = getFoldersUseCase,
            createFolderUseCase = createFolderUseCase,
            setNoteFolderUseCase = setNoteFolderUseCase,
            renameNoteUseCase = renameNoteUseCase,
            deleteNoteUseCase = deleteNoteUseCase,
            createNewTaskUseCase = createNewTaskUseCase,
            renameTaskUseCase = renameTaskUseCase,
            updateTaskStatusUseCase = updateTaskStatusUseCase,
            deleteTaskUseCase = deleteTaskUseCase,
            syncNoteStatusUseCase = syncNoteStatusUseCase
        )
    }

    protected fun advanceUntilIdle() {
        testDispatcher.scheduler.advanceUntilIdle()
    }

    protected fun invokeViewModelMethod(name: String, vararg args: Any?) {
        val method = viewModel.javaClass.methods.firstOrNull {
            it.name == name && it.parameterCount == args.size
        } ?: viewModel.javaClass.declaredMethods.firstOrNull {
            it.name == name && it.parameterCount == args.size
        }?.also { it.isAccessible = true }

        assertNotNull(method, "Expected NoteScreenViewModel method '$name' to exist")
        method.invoke(viewModel, *args)
    }

    protected fun assertErrorState(): UiState.Error<NoteScreenUiErrors> {
        return assertIs(viewModel.uiState.value)
    }
}

internal const val NOTE_ID = 11L
internal const val MISSING_TASK_ID = 999L

internal val baseTask = NoteTask(
    id = 77L,
    title = "Base Task",
    status = NoteTaskStatus.IN_PROGRESS
)

internal val baseNote = Note(
    id = NOTE_ID,
    title = "Test Note",
    status = NoteStatus.IN_PROGRESS,
    folderId = 5L,
    tasksList = listOf(baseTask),
    createdTimestamp = "2024-01-01T10:00:00Z",
    lastUpdatedTimestamp = "2024-01-01T10:00:00Z"
)
