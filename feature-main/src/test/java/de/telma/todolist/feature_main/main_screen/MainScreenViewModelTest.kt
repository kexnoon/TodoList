package de.telma.todolist.feature_main.main_screen

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesUseCase
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
    fun `onSearchQueryInput should update search flow and eventually call getAllNotes`() = runTest {
        val query = "test query"
        coEvery { getNotesUseCase(any()) } returns flowOf(emptyList())

        viewModel.onSearchQueryInput(query)
        
        assertEquals(query, viewModel.search.value.query)
        
        // Advance time for debounce (300ms)
        advanceTimeBy(301)
        testDispatcher.scheduler.runCurrent()
        
        coVerify { getNotesUseCase(match { it.query == query }) }
    }

    @Test
    fun `onClearSearchPressed should reset search query`() = runTest {
        viewModel.onSearchQueryInput("some text")
        viewModel.onClearSearchClicked()
        
        assertEquals("", viewModel.search.value.query)
    }

    @Test
    fun `onNoteSelected should enable selection mode and update count`() = runTest {
        val noteId = 1L
        val notes = listOf(
            Note(id = 1L, title = "Note 1", status = NoteStatus.IN_PROGRESS, lastUpdatedTimestamp = "", createdTimestamp = "", tasksList = listOf()),
            Note(id = 2L, title = "Note 2", status = NoteStatus.IN_PROGRESS, lastUpdatedTimestamp = "", createdTimestamp = "", tasksList = listOf())
        )
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
        val notes = listOf(
            Note(id = 1L, title = "Note 1", status = NoteStatus.IN_PROGRESS, lastUpdatedTimestamp = "", createdTimestamp = "", tasksList = listOf())
        )
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
}
