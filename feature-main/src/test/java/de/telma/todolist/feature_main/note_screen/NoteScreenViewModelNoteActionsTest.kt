package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.component_notes.useCase.note.RenameNoteUseCase
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.core_ui.navigation.NavEvent
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NoteScreenViewModelNoteActionsTest : NoteScreenViewModelTestBase() {

    @Test
    fun delete_note_should_dismiss_dialog_and_navigate_back_on_success() = runTest {
        coEvery { deleteNoteUseCase(baseNote) } returns true

        viewModel.deleteNote()
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        coVerify(exactly = 1) { coordinator.execute(NavEvent.PopBack) }
    }

    @Test
    fun delete_note_should_show_error_on_failure() = runTest {
        coEvery { deleteNoteUseCase(baseNote) } returns false

        viewModel.deleteNote()
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToDeleteNote(NOTE_ID), error.uiError)
    }

    @Test
    fun rename_note_should_dismiss_dialog_and_show_error_when_use_case_fails() = runTest {
        coEvery { renameNoteUseCase(baseNote, "Renamed") } returns RenameNoteUseCase.Result.FAILURE

        viewModel.renameNote("Renamed")
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToRenameNote(NOTE_ID), error.uiError)
    }

    @Test
    fun rename_note_should_call_sync_on_success() = runTest {
        coEvery { renameNoteUseCase(baseNote, "Renamed") } returns RenameNoteUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel.renameNote("Renamed")
        advanceUntilIdle()

        coVerify(exactly = 1) { syncNoteStatusUseCase(NOTE_ID) }
    }

    @Test
    fun rename_note_should_show_sync_error_when_sync_fails() = runTest {
        coEvery { renameNoteUseCase(baseNote, "Renamed") } returns RenameNoteUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.SyncFailed

        viewModel.renameNote("Renamed")
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToSyncNoteStatus(NOTE_ID), error.uiError)
    }
}
