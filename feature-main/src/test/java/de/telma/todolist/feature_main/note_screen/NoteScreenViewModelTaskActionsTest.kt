package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.task.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.task.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.task.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.task.UpdateTaskStatusUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NoteScreenViewModelTaskActionsTest : NoteScreenViewModelTestBase() {

    @Test
    fun update_task_status_should_show_task_not_found_when_task_id_is_missing() = runTest {
        viewModel.updateTaskStatus(MISSING_TASK_ID)
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.TaskNotFound(MISSING_TASK_ID), error.uiError)
    }

    @Test
    fun update_task_status_should_show_failure_error_when_use_case_fails() = runTest {
        coEvery {
            updateTaskStatusUseCase(NOTE_ID, baseTask, NoteTaskStatus.COMPLETE)
        } returns UpdateTaskStatusUseCase.Result.FAILURE

        viewModel.updateTaskStatus(baseTask.id)
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToUpdateTaskStatus(baseTask.id), error.uiError)
    }

    @Test
    fun update_task_status_should_call_sync_on_success() = runTest {
        coEvery {
            updateTaskStatusUseCase(NOTE_ID, baseTask, NoteTaskStatus.COMPLETE)
        } returns UpdateTaskStatusUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel.updateTaskStatus(baseTask.id)
        advanceUntilIdle()

        coVerify(exactly = 1) { syncNoteStatusUseCase(NOTE_ID) }
    }

    @Test
    fun update_task_status_should_show_sync_error_when_sync_fails() = runTest {
        coEvery {
            updateTaskStatusUseCase(NOTE_ID, baseTask, NoteTaskStatus.COMPLETE)
        } returns UpdateTaskStatusUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.SyncFailed

        viewModel.updateTaskStatus(baseTask.id)
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToSyncNoteStatus(NOTE_ID), error.uiError)
    }

    @Test
    fun update_task_status_should_reload_note_when_sync_succeeds() = runTest {
        coEvery {
            updateTaskStatusUseCase(NOTE_ID, baseTask, NoteTaskStatus.COMPLETE)
        } returns UpdateTaskStatusUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.SyncSucceed

        viewModel.updateTaskStatus(baseTask.id)
        advanceUntilIdle()

        coVerify(exactly = 2) { noteRepository.getNoteById(NOTE_ID) }
    }

    @Test
    fun add_task_should_dismiss_dialog_and_show_error_when_use_case_fails() = runTest {
        coEvery { createNewTaskUseCase(baseNote, "Task title") } returns CreateNewTaskUseCase.Result.FAILURE

        viewModel.addTask("Task title")
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToCreateNewTask, error.uiError)
    }

    @Test
    fun add_task_should_call_sync_on_success() = runTest {
        coEvery { createNewTaskUseCase(baseNote, "Task title") } returns CreateNewTaskUseCase.Result.SUCCESS(NOTE_ID)
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel.addTask("Task title")
        advanceUntilIdle()

        coVerify(exactly = 1) { syncNoteStatusUseCase(NOTE_ID) }
    }

    @Test
    fun add_task_should_show_sync_error_when_sync_fails() = runTest {
        coEvery { createNewTaskUseCase(baseNote, "Task title") } returns CreateNewTaskUseCase.Result.SUCCESS(NOTE_ID)
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.SyncFailed

        viewModel.addTask("Task title")
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToSyncNoteStatus(NOTE_ID), error.uiError)
    }

    @Test
    fun rename_task_should_dismiss_dialog_and_show_task_not_found_when_task_is_missing() = runTest {
        viewModel.renameTask(MISSING_TASK_ID, "New title")
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.TaskNotFound(MISSING_TASK_ID), error.uiError)
    }

    @Test
    fun rename_task_should_show_error_when_use_case_fails() = runTest {
        coEvery { renameTaskUseCase(NOTE_ID, baseTask, "New title") } returns RenameTaskUseCase.Result.FAILURE

        viewModel.renameTask(baseTask.id, "New title")
        advanceUntilIdle()

        assertEquals(NoteScreenUiEvents.DismissDialog, viewModel.uiEvents.value)
        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToRenameTask(baseTask.id), error.uiError)
    }

    @Test
    fun rename_task_should_call_sync_on_success() = runTest {
        coEvery { renameTaskUseCase(NOTE_ID, baseTask, "New title") } returns RenameTaskUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel.renameTask(baseTask.id, "New title")
        advanceUntilIdle()

        coVerify(exactly = 1) { syncNoteStatusUseCase(NOTE_ID) }
    }

    @Test
    fun delete_task_should_show_task_not_found_when_task_is_missing() = runTest {
        viewModel.deleteTask(MISSING_TASK_ID)
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.TaskNotFound(MISSING_TASK_ID), error.uiError)
    }

    @Test
    fun delete_task_should_show_error_when_use_case_fails() = runTest {
        coEvery { deleteTaskUseCase(NOTE_ID, baseTask) } returns DeleteTaskUseCase.Result.FAILURE

        viewModel.deleteTask(baseTask.id)
        advanceUntilIdle()

        val error = assertErrorState()
        assertEquals(NoteScreenUiErrors.FailedToDeleteTask(baseTask.id), error.uiError)
    }

    @Test
    fun delete_task_should_call_sync_on_success() = runTest {
        coEvery { deleteTaskUseCase(NOTE_ID, baseTask) } returns DeleteTaskUseCase.Result.SUCCESS
        coEvery { syncNoteStatusUseCase(NOTE_ID) } returns SyncNoteStatusUseCase.Result.UpToDate

        viewModel.deleteTask(baseTask.id)
        advanceUntilIdle()

        coVerify(exactly = 1) { syncNoteStatusUseCase(NOTE_ID) }
    }
}
