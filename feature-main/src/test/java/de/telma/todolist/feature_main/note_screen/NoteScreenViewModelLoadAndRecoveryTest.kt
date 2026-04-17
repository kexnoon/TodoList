package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.core_ui.state.UiState
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class NoteScreenViewModelLoadAndRecoveryTest : NoteScreenViewModelTestBase() {

    @Test
    fun init_should_map_note_data_to_result_state() = runTest {
        val state = assertIs<UiState.Result<NoteScreenState>>(viewModel.uiState.value).data

        assertEquals(NOTE_ID, state.noteId)
        assertEquals(baseNote.title, state.appBar.title)
        assertEquals(false, state.appBar.isComplete)
        assertEquals(baseTask.id, state.tasks.first().id)
        assertEquals(baseTask.title, state.tasks.first().title)
        assertEquals(false, state.tasks.first().isCompleted)
    }

    @Test
    fun init_should_show_notenotfound_when_repository_returns_null() = runTest {
        coEvery { noteRepository.getNoteById(NOTE_ID) } returns flowOf(null)

        viewModel = createViewModel()
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.NoteNotFound(NOTE_ID), error.uiError)
    }

    @Test
    fun retry_on_error_should_request_note_again_and_recover_result_state() = runTest {
        clearMocks(noteRepository, answers = false)
        coEvery { noteRepository.getNoteById(NOTE_ID) } returnsMany listOf(flowOf(null), flowOf(baseNote))
        viewModel = createViewModel()
        advanceUntilIdle()
        assertIs<UiState.Error<NoteScreenUiErrors>>(viewModel.uiState.value)

        viewModel.retryOnError()
        advanceUntilIdle()

        coVerify(exactly = 2) { noteRepository.getNoteById(NOTE_ID) }
        assertIs<UiState.Result<NoteScreenState>>(viewModel.uiState.value)
    }
}
