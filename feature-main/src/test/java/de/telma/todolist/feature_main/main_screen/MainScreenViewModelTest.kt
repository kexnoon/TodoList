package de.telma.todolist.feature_main.main_screen

import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.DeleteFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.GetFoldersUseCase
import de.telma.todolist.component_notes.useCase.folder.RenameFolderUseCase
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesUseCase
import de.telma.todolist.component_notes.useCase.note.MoveNotesToFolderUseCase
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var coordinator: NavigationCoordinator
    private lateinit var getNotesUseCase: GetNotesUseCase
    private lateinit var getFoldersUseCase: GetFoldersUseCase
    private lateinit var createFolderUseCase: CreateFolderUseCase
    private lateinit var renameFolderUseCase: RenameFolderUseCase
    private lateinit var deleteFolderUseCase: DeleteFolderUseCase
    private lateinit var createNewNoteUseCase: CreateNewNoteUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var moveNotesToFolderUseCase: MoveNotesToFolderUseCase
    
    private lateinit var viewModel: MainScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        coordinator = mockk(relaxed = true)
        getNotesUseCase = mockk()
        getFoldersUseCase = mockk()
        createFolderUseCase = mockk()
        renameFolderUseCase = mockk()
        deleteFolderUseCase = mockk()
        createNewNoteUseCase = mockk()
        deleteNoteUseCase = mockk()
        moveNotesToFolderUseCase = mockk()
        
        // Default mock for init
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())
        every { getFoldersUseCase() } returns flowOf(emptyList())
        
        viewModel = MainScreenViewModel(
            coordinator,
            getNotesUseCase,
            getFoldersUseCase,
            createFolderUseCase,
            renameFolderUseCase,
            deleteFolderUseCase,
            createNewNoteUseCase,
            deleteNoteUseCase,
            moveNotesToFolderUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call getAllNotes and show Result state`() = runTest {
        val notes = listOf(mockk<Note>(relaxed = true))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        
        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Result)
        assertEquals(notes.size, state.data.notes.size)
    }

    @Test
    fun `onSearchQueryInput should update search flow and call getAllNotes with query`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val query = "test query"
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())

        viewModel.onSearchQueryInput(query)
        
        assertEquals(query, viewModel.search.value.query)
        
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()
        
        coVerify(exactly = 1) { getNotesUseCase(match { it.query == query }, null) }
    }

    @Test
    fun `onClearSearchClicked should reset search model and fetch default`() = runTest {
        viewModel.onSearchQueryInput("some text")
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        clearMocks(getNotesUseCase, answers = false)

        viewModel.onClearSearchClicked()
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()
        
        assertEquals(SearchModel(), viewModel.search.value)
        coVerify(exactly = 1) { getNotesUseCase(SearchModel(), null) }
    }

    @Test
    fun `onNoteSelected should enable selection mode and update count`() = runTest {
        val noteId = 1L
        val notes = listOf(testNote.copy(id = noteId), testNote.copy(id = 2L))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        
        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onNoteSelected(noteId, true)
        
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(state.isSelectionMode)
        assertEquals(1, state.selectedNotesCount)
        assertTrue(state.notes.find { it.id == noteId }?.isSelected == true)
    }

    @Test
    fun `onClearSelectionClicked should disable selection mode and reset count`() = runTest {
        viewModel.onNoteSelected(1L, true)
        viewModel.onClearSelectionClicked()
        
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isSelectionMode)
        assertEquals(0, state.selectedNotesCount)
        assertTrue(state.notes.all { !it.isSelected })
    }

    @Test
    fun `deleteSelectedNotes should call useCase and clear selection on success`() = runTest {
        val notes = listOf(testNote.copy())
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { deleteNoteUseCase(any<List<Note>>()) } returns DeleteNoteUseCase.Result.SUCCESS
        
        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        
        viewModel.onNoteSelected(1L, true)
        viewModel.deleteSelectedNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { deleteNoteUseCase(any<List<Note>>()) }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isSelectionMode)
    }

    @Test
    fun `onSearchModelUpdate should apply filters and fetch notes`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val filters = Filters(status = NoteStatus.COMPLETE)
        val searchModel = SearchModel(filters = filters)

        viewModel.onSearchModelUpdate(searchModel)
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        assertEquals(filters, viewModel.search.value.filters)
        assertEquals(null, viewModel.search.value.query)
        coVerify(exactly = 1) { getNotesUseCase(match { it.filters == filters && it.query == null }, null) }
    }

    @Test
    fun `onSearchModelUpdate should request notes with updated sortBy`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val searchModel = SearchModel(sortBy = SortBy.TITLE)

        viewModel.onSearchModelUpdate(searchModel)
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        assertEquals(SortBy.TITLE, viewModel.search.value.sortBy)
        coVerify(exactly = 1) { getNotesUseCase(match { it.sortBy == SortBy.TITLE }, null) }
    }

    @Test
    fun `onSearchModelUpdate should request notes with updated sortOrder`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val searchModel = SearchModel(sortOrder = SortOrder.ASC)

        viewModel.onSearchModelUpdate(searchModel)
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        assertEquals(SortOrder.ASC, viewModel.search.value.sortOrder)
        coVerify(exactly = 1) { getNotesUseCase(match { it.sortOrder == SortOrder.ASC }, null) }
    }

    @Test
    fun `init should expose folders from repository`() = runTest {
        val folders = listOf(
            Folder(id = 1L, name = "Work", lastUpdatedTimestamp = "2024-01-02T10:00:00Z"),
            Folder(id = 2L, name = "Home", lastUpdatedTimestamp = "2024-01-01T10:00:00Z")
        )
        every { getFoldersUseCase() } returns flowOf(folders)
        viewModel = MainScreenViewModel(
            coordinator,
            getNotesUseCase,
            getFoldersUseCase,
            createFolderUseCase,
            renameFolderUseCase,
            deleteFolderUseCase,
            createNewNoteUseCase,
            deleteNoteUseCase,
            moveNotesToFolderUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()

        val state = (viewModel.uiState.value as UiState.Result).data
        assertEquals(folders, state.folders)
    }

    @Test
    fun `getAllNotes should request notes from selected folder when query is empty`() = runTest {
        recreateViewModelWithFolders(listOf(testFolder(7L)))
        setSelectedFolderId(7L)
        clearMocks(getNotesUseCase, answers = false)
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { getNotesUseCase(match { it.query == null }, 7L) }
    }

    @Test
    fun `active search should be global and hide folder chip row`() = runTest {
        recreateViewModelWithFolders(listOf(testFolder(7L)))
        setSelectedFolderId(7L)
        clearMocks(getNotesUseCase, answers = false)
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())

        viewModel.onSearchQueryInput("work")
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { getNotesUseCase(match { it.query == "work" }, null) }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isFolderChipRowVisible)
    }

    @Test
    fun `clearing search should return notes request to previously selected folder`() = runTest {
        recreateViewModelWithFolders(listOf(testFolder(7L)))
        setSelectedFolderId(7L)
        clearMocks(getNotesUseCase, answers = false)
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())
        viewModel.onSearchQueryInput("work")
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        clearMocks(getNotesUseCase, answers = false)
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(emptyList())

        viewModel.onClearSearchClicked()
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        coVerify(exactly = 1) { getNotesUseCase(match { it.query == null }, 7L) }
    }

    @Test
    fun `createNewNote should pass selectedFolderId`() = runTest {
        recreateViewModelWithFolders(listOf(testFolder(9L)))
        setSelectedFolderId(9L)
        coEvery { createNewNoteUseCase(any(), any()) } returns CreateNewNoteUseCase.Result.SUCCESS(42L)

        viewModel.createNewNote("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { createNewNoteUseCase("Test", 9L) }
    }

    @Test
    fun `createNewNote should pass null folderId when All is selected`() = runTest {
        setSelectedFolderId(null)
        coEvery { createNewNoteUseCase(any(), any()) } returns CreateNewNoteUseCase.Result.SUCCESS(42L)

        viewModel.createNewNote("Test")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { createNewNoteUseCase("Test", null) }
    }

    @Test
    fun `onMoveToFolderClicked should emit show move dialog event`() = runTest {
        viewModel.onMoveToFolderClicked()

        assertEquals(MainScreenUiEvents.ShowMoveToFolderDialog, viewModel.uiEvents.value)
    }

    @Test
    fun `dismissMoveToFolderDialog should emit dismiss move dialog event`() = runTest {
        viewModel.dismissMoveToFolderDialog()

        assertEquals(MainScreenUiEvents.DismissMoveToFolderDialog, viewModel.uiEvents.value)
    }

    @Test
    fun `onMoveToNoFolderConfirmed should move selected notes and exit selection mode on success`() = runTest {
        val notes = listOf(
            testNote.copy(id = 1L),
            testNote.copy(id = 2L)
        )
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { moveNotesToFolderUseCase(any(), null) } returns MoveNotesToFolderUseCase.Result.SUCCESS

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(1L, true)
        viewModel.onMoveToNoFolderConfirmed()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            moveNotesToFolderUseCase(match { selected -> selected.map { it.id } == listOf(1L) }, null)
        }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isSelectionMode)
    }

    @Test
    fun `onMoveToFolderConfirmed should move selected notes to target folder and exit selection mode on success`() = runTest {
        val targetFolderId = 9L
        val notes = listOf(
            testNote.copy(id = 1L),
            testNote.copy(id = 2L)
        )
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { moveNotesToFolderUseCase(any(), targetFolderId) } returns MoveNotesToFolderUseCase.Result.SUCCESS

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(2L, true)
        viewModel.onMoveToFolderConfirmed(targetFolderId)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            moveNotesToFolderUseCase(match { selected -> selected.map { it.id } == listOf(2L) }, targetFolderId)
        }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isSelectionMode)
    }

    @Test
    fun `new folder move flow should create folder and move selected notes on success`() = runTest {
        val createdFolderId = 77L
        val notes = listOf(testNote.copy(id = 11L))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { createFolderUseCase("Work") } returns CreateFolderUseCase.Result.SUCCESS(createdFolderId)
        coEvery { moveNotesToFolderUseCase(any(), createdFolderId) } returns MoveNotesToFolderUseCase.Result.SUCCESS

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(11L, true)
        viewModel.onCreateFolderForMoveClicked()
        assertEquals(MainScreenUiEvents.ShowCreateFolderForMoveDialog, viewModel.uiEvents.value)

        viewModel.createFolderForMove("Work")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { createFolderUseCase("Work") }
        coVerify(exactly = 1) {
            moveNotesToFolderUseCase(match { selected -> selected.map { it.id } == listOf(11L) }, createdFolderId)
        }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(!state.isSelectionMode)
    }

    @Test
    fun `new folder invalid name should keep selection mode active`() = runTest {
        val notes = listOf(testNote.copy(id = 12L))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { createFolderUseCase("   ") } returns CreateFolderUseCase.Result.INVALID_NAME

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(12L, true)
        viewModel.createFolderForMove("   ")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { moveNotesToFolderUseCase(any(), any()) }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `new folder create failure should keep selection mode active`() = runTest {
        val notes = listOf(testNote.copy(id = 14L))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { createFolderUseCase("Work") } returns CreateFolderUseCase.Result.FAILURE

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(14L, true)
        viewModel.createFolderForMove("Work")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { moveNotesToFolderUseCase(any(), any()) }
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `move failure should keep selection mode active and emit move flow error event`() = runTest {
        val notes = listOf(testNote.copy(id = 13L))
        coEvery { getNotesUseCase(any(), any()) } returns flowOf(notes)
        coEvery { moveNotesToFolderUseCase(any(), 3L) } returns MoveNotesToFolderUseCase.Result.FAILURE

        viewModel.getAllNotes()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.onNoteSelected(13L, true)
        viewModel.onMoveToFolderConfirmed(3L)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(MainScreenUiEvents.ShowMoveFlowError, viewModel.uiEvents.value)
        val state = (viewModel.uiState.value as UiState.Result).data
        assertTrue(state.isSelectionMode)
    }

    @Test
    fun `dismissMoveFlowErrorEvent should emit dismiss move flow error event`() = runTest {
        viewModel.dismissMoveFlowErrorEvent()

        assertEquals(MainScreenUiEvents.DismissMoveFlowError, viewModel.uiEvents.value)
    }

    @Test
    fun `dismissFolderFlowErrorEvent should emit dismiss folder flow error event`() = runTest {
        viewModel.dismissFolderFlowErrorEvent()

        assertEquals(MainScreenUiEvents.DismissFolderFlowError, viewModel.uiEvents.value)
    }

    private fun setSelectedFolderId(folderId: Long?) {
        viewModel.onFolderSelected(folderId)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun recreateViewModelWithFolders(folders: List<Folder>) {
        every { getFoldersUseCase() } returns flowOf(folders)
        viewModel = MainScreenViewModel(
            coordinator,
            getNotesUseCase,
            getFoldersUseCase,
            createFolderUseCase,
            renameFolderUseCase,
            deleteFolderUseCase,
            createNewNoteUseCase,
            deleteNoteUseCase,
            moveNotesToFolderUseCase
        )
        testDispatcher.scheduler.advanceUntilIdle()
    }

    private fun testFolder(id: Long): Folder {
        return Folder(id = id, name = "Folder $id", lastUpdatedTimestamp = "2024-01-01T00:00:00Z")
    }
}

private val testNote = Note(
    id = 1L,
    title = "Note 1",
    status = NoteStatus.IN_PROGRESS,
    lastUpdatedTimestamp = "",
    createdTimestamp = "",
    tasksList = listOf()
)


