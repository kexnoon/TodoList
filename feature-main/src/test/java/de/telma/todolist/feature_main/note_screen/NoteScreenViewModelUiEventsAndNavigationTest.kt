package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.core_ui.navigation.NavEvent
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NoteScreenViewModelUiEventsAndNavigationTest : NoteScreenViewModelTestBase() {

    @Test
    fun on_delete_note_pressed_should_emit_show_delete_note_dialog_event() = runTest {
        viewModel.onDeleteNotePressed()

        assertEquals(NoteScreenUiEvents.ShowDeleteNoteDialog, viewModel.uiEvents.value)
    }

    @Test
    fun on_rename_note_pressed_should_emit_show_note_rename_dialog_with_current_title() = runTest {
        viewModel.onRenameNotePressed()

        assertEquals(
            NoteScreenUiEvents.ShowNoteRenameDialog(baseNote.title),
            viewModel.uiEvents.value
        )
    }

    @Test
    fun on_add_task_pressed_should_emit_show_add_task_dialog_event() = runTest {
        viewModel.onAddTaskPressed()

        assertEquals(NoteScreenUiEvents.ShowAddTaskDialog, viewModel.uiEvents.value)
    }

    @Test
    fun on_task_rename_pressed_should_emit_rename_dialog_event_for_existing_task() = runTest {
        viewModel.onTaskRenamePressed(baseTask.id)

        assertEquals(
            NoteScreenUiEvents.ShowTaskRenameDialog(baseTask.id, baseTask.title),
            viewModel.uiEvents.value
        )
    }

    @Test
    fun on_task_rename_pressed_should_show_task_not_found_for_missing_task() = runTest {
        viewModel.onTaskRenamePressed(MISSING_TASK_ID)

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.TaskNotFound(MISSING_TASK_ID), error.uiError)
    }

    @Test
    fun dismiss_dialog_should_emit_dismiss_dialog_event() = runTest {
        viewModel.dismissDialog()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
    }

    @Test
    fun onbackpressed_should_navigate_back() = runTest {
        viewModel.onBackPressed()
        advanceUntilIdle()

        coVerify(exactly = 1) { coordinator.execute(NavEvent.PopBack) }
    }

    @Test
    fun oncreatefolderpressed_should_emit_showcreatefolderdialog_event() = runTest {
        viewModel.onCreateFolderPressed()

        assertEquals(NoteScreenUiEvents.ShowCreateFolderDialog, viewModel.uiEvents.value)
    }

    @Test
    fun dismisscreatefolderdialog_should_emit_dismisscreatefolderdialog_event() = runTest {
        viewModel.dismissCreateFolderDialog()

        assertEquals(NoteScreenUiEvents.DismissCreateFolderDialog, viewModel.uiEvents.value)
    }
}
