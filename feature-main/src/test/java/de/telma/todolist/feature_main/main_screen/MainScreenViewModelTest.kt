package de.telma.todolist.feature_main.main_screen

import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesUseCase
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.UiState
import io.mockk.coEvery
import io.mockk.clearMocks
import io.mockk.coVerify
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
    private lateinit var createNewNoteUseCase: CreateNewNoteUseCase
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    
    private lateinit var viewModel: MainScreenViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        coordinator = mockk(relaxed = true)
        getNotesUseCase = mockk()
        createNewNoteUseCase = mockk()
        deleteNoteUseCase = mockk()
        
        // Default mock for init
        coEvery { getNotesUseCase(any()) } returns flowOf(emptyList())
        
        viewModel = MainScreenViewModel(
            coordinator,
            getNotesUseCase,
            createNewNoteUseCase,
            deleteNoteUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should call getAllNotes and show Result state`() = runTest {
        val notes = listOf(mockk<Note>(relaxed = true))
        coEvery { getNotesUseCase(any()) } returns flowOf(notes)
        
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
        coEvery { getNotesUseCase(any()) } returns flowOf(emptyList())

        viewModel.onSearchQueryInput(query)
        
        assertEquals(query, viewModel.search.value.query)
        
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()
        
        coVerify(exactly = 1) { getNotesUseCase(match { it.query == query }) }
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
        coVerify(exactly = 1) { getNotesUseCase(SearchModel()) }
    }

    @Test
    fun `onNoteSelected should enable selection mode and update count`() = runTest {
        val noteId = 1L
        val notes = listOf(testNote.copy(id = noteId), testNote.copy(id = 2L))
        coEvery { getNotesUseCase(any()) } returns flowOf(notes)
        
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
        coEvery { getNotesUseCase(any()) } returns flowOf(notes)
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
        coVerify(exactly = 1) { getNotesUseCase(match { it.filters == filters && it.query == null }) }
    }

    @Test
    fun `onSearchModelUpdate should request notes with updated sortBy`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val searchModel = SearchModel(sortBy = SortBy.TITLE)

        viewModel.onSearchModelUpdate(searchModel)
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        assertEquals(SortBy.TITLE, viewModel.search.value.sortBy)
        coVerify(exactly = 1) { getNotesUseCase(match { it.sortBy == SortBy.TITLE }) }
    }

    @Test
    fun `onSearchModelUpdate should request notes with updated sortOrder`() = runTest {
        clearMocks(getNotesUseCase, answers = false)
        val searchModel = SearchModel(sortOrder = SortOrder.ASC)

        viewModel.onSearchModelUpdate(searchModel)
        testDispatcher.scheduler.advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()

        assertEquals(SortOrder.ASC, viewModel.search.value.sortOrder)
        coVerify(exactly = 1) { getNotesUseCase(match { it.sortOrder == SortOrder.ASC }) }
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


