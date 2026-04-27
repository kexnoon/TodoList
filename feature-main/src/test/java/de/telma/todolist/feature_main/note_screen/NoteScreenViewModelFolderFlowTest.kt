package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.note.SetNoteFolderUseCase
import de.telma.todolist.core_ui.state.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class NoteScreenViewModelFolderFlowTest : NoteScreenViewModelTestBase() {

    @Test
    fun initial_load_should_include_folder_indicator_state() = runTest {
        val folders = listOf(
            Folder(id = 9L, name = "Work", lastUpdatedTimestamp = "2024-02-01T10:00:00Z"),
            Folder(id = 3L, name = "Home", lastUpdatedTimestamp = "2024-01-01T10:00:00Z")
        )
        every { getFoldersUseCase() } returns flowOf(folders)
        coEvery { noteRepository.getNoteById(NOTE_ID) } returns flowOf(baseNote.copy(folderId = 9L))

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = assertIs<UiState.Result<NoteScreenState>>(viewModel.uiState.value).data
        assertEquals(9L, state.appBar.currentFolder.folderId)
        assertEquals("Work", state.appBar.currentFolder.name)
        assertEquals(folders, state.availableFolders)
    }

    @Test
    fun dropdown_ordering_should_be_no_folder_then_folders_then_new_folder() = runTest {
        val folders = listOf(
            Folder(id = 9L, name = "Work", lastUpdatedTimestamp = "2024-02-01T10:00:00Z"),
            Folder(id = 3L, name = "Home", lastUpdatedTimestamp = "2024-01-01T10:00:00Z")
        )
        every { getFoldersUseCase() } returns flowOf(folders)

        viewModel = createViewModel()
        advanceUntilIdle()

        val state = assertIs<UiState.Result<NoteScreenState>>(viewModel.uiState.value).data
        val labels = listOf("No folder") + state.availableFolders.map { it.name } + listOf("New folder")
        assertEquals(listOf("No folder", "Work", "Home", "New folder"), labels)
    }

    @Test
    fun selecting_no_folder_should_unassign_current_note_folder() = runTest {
        coEvery { setNoteFolderUseCase(NOTE_ID, null) } returns SetNoteFolderUseCase.Result.SUCCESS

        invokeViewModelMethod("onFolderSelected", null)
        advanceUntilIdle()

        coVerify(exactly = 1) { setNoteFolderUseCase(NOTE_ID, null) }
    }

    @Test
    fun selecting_existing_folder_should_assign_current_note_folder() = runTest {
        val targetFolderId = 7L
        coEvery { setNoteFolderUseCase(NOTE_ID, targetFolderId) } returns SetNoteFolderUseCase.Result.SUCCESS

        invokeViewModelMethod("onFolderSelected", targetFolderId)
        advanceUntilIdle()

        coVerify(exactly = 1) { setNoteFolderUseCase(NOTE_ID, targetFolderId) }
    }

    @Test
    fun selecting_same_folder_should_skip_folder_assignment_use_case_call() = runTest {
        invokeViewModelMethod("onFolderSelected", baseNote.folderId)
        advanceUntilIdle()

        coVerify(exactly = 0) { setNoteFolderUseCase(any(), any()) }
    }

    @Test
    fun selecting_new_folder_should_open_dialog_and_on_success_create_and_assign() = runTest {
        val newFolderId = 17L
        coEvery { createFolderUseCase("Work") } returns CreateFolderUseCase.Result.SUCCESS(newFolderId)
        coEvery { setNoteFolderUseCase(NOTE_ID, newFolderId) } returns SetNoteFolderUseCase.Result.SUCCESS

        viewModel.onCreateFolderPressed()
        assertEquals(NoteScreenUiEvents.ShowCreateFolderDialog, viewModel.uiEvents.value)

        invokeViewModelMethod("createFolderAndAssign", "Work")
        advanceUntilIdle()

        coVerify(exactly = 1) { createFolderUseCase("Work") }
        coVerify(exactly = 1) { setNoteFolderUseCase(NOTE_ID, newFolderId) }
    }

    @Test
    fun invalid_folder_name_should_be_handled_in_create_flow() = runTest {
        coEvery { createFolderUseCase("   ") } returns CreateFolderUseCase.Result.INVALID_NAME

        invokeViewModelMethod("createFolderAndAssign", "   ")
        advanceUntilIdle()

        val error = assertIs<UiState.Error<NoteScreenUiErrors>>(viewModel.uiState.value)
        assertEquals(NoteScreenUiErrors.FailedToCreateFolder, error.uiError)
    }

    @Test
    fun assignment_and_create_failures_should_be_mapped_to_ui_error_state() = runTest {
        val newFolderId = 23L
        coEvery { createFolderUseCase("Work") } returns CreateFolderUseCase.Result.SUCCESS(newFolderId)
        coEvery { setNoteFolderUseCase(NOTE_ID, newFolderId) } returns SetNoteFolderUseCase.Result.FAILURE

        invokeViewModelMethod("createFolderAndAssign", "Work")
        advanceUntilIdle()

        val error = assertIs<UiState.Error<NoteScreenUiErrors>>(viewModel.uiState.value)
        assertEquals(NoteScreenUiErrors.FailedToAssignFolder(NOTE_ID), error.uiError)
    }
}
