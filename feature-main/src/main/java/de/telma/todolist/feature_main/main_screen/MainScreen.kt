package de.telma.todolist.feature_main.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.BasicDialog
import de.telma.todolist.core_ui.composables.InputDialog
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.main_screen.composables.FilterDialog
import de.telma.todolist.feature_main.main_screen.composables.MoveToFolderDialog
import de.telma.todolist.feature_main.main_screen.states.*
@Composable
internal fun MainScreen(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEvents by viewModel.uiEvents.collectAsState()
    val searchModel by viewModel.search.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNewNoteDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var showMoveToFolderDialog by remember { mutableStateOf(false) }
    var showCreateFolderForMoveDialog by remember { mutableStateOf(false) }
    var showRenameFolderDialog by remember { mutableStateOf(false) }
    var showDeleteFolderDialog by remember { mutableStateOf(false) }
    var showMoveFlowErrorDialog by remember { mutableStateOf(false) }
    var showFolderFlowErrorDialog by remember { mutableStateOf(false) }
    var currentFolderId by remember { mutableStateOf<Long?>(null) }
    var currentFolderName by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val result = uiState) {
            is UiState.Loading -> StateLoading(modifier = Modifier.fillMaxSize())
            is UiState.Result<MainScreenState> -> StateResult(
                modifier = Modifier.fillMaxSize(),
                result = result.data,
                searchModel = searchModel,
                onDeleteClick = { viewModel.onDeleteClicked() },
                onMoveToFolderClick = { viewModel.onMoveToFolderClicked() },
                onClearSelectionClick = { viewModel.onClearSelectionClicked() },
                onNewNoteClick = { viewModel.onNewNoteClicked() },
                onItemClick = { viewModel.toDetailsScreen(it) },
                onItemSelected = { id, isSelected -> viewModel.onNoteSelected(id, isSelected) },
                onSearchInput = { viewModel.onSearchQueryInput(it) },
                onSearchClear = { viewModel.onClearSearchClicked() },
                onSearchFilterClicked = { viewModel.showFilterDialog() },
                onSortUpdate = { viewModel.onSearchModelUpdate(it) },
                onFolderSelected = { folderId -> viewModel.onFolderSelected(folderId) },
                onNewFolderPressed = { viewModel.onNewFolderPressed() },
                onFolderRenameRequest = { folderId -> viewModel.onFolderRenameRequested(folderId) },
                onFolderDeleteRequest = { folderId -> viewModel.onFolderDeleteRequested(folderId) }
            )

            is UiState.Error -> StateError(
                modifier = Modifier.fillMaxSize(),
                error = (uiState as UiState.Error<MainScreenUiErrors>).uiError,
                onRetryPressed = { viewModel.retryOnError() }
            )
        }

        val event = uiEvents
        when (event) {
            is MainScreenUiEvents.ShowDeleteDialog -> showDeleteDialog = true
            is MainScreenUiEvents.DismissDeleteDialog -> showDeleteDialog = false
            is MainScreenUiEvents.ShowCreateNewNoteDialog -> showNewNoteDialog = true
            is MainScreenUiEvents.DismissCreateNewNoteDialog -> showNewNoteDialog = false
            is MainScreenUiEvents.ShowFilterDialog -> showFilterDialog = true
            is MainScreenUiEvents.DismissFilterDialog -> showFilterDialog = false
            is MainScreenUiEvents.ShowCreateFolderDialog -> showCreateFolderDialog = true
            is MainScreenUiEvents.DismissCreateFolderDialog -> showCreateFolderDialog = false
            is MainScreenUiEvents.ShowMoveToFolderDialog -> showMoveToFolderDialog = true
            is MainScreenUiEvents.DismissMoveToFolderDialog -> showMoveToFolderDialog = false
            is MainScreenUiEvents.ShowCreateFolderForMoveDialog -> {
                showMoveToFolderDialog = false
                showCreateFolderForMoveDialog = true
            }
            is MainScreenUiEvents.DismissCreateFolderForMoveDialog -> showCreateFolderForMoveDialog = false
            is MainScreenUiEvents.ShowMoveFlowError -> {
                showMoveFlowErrorDialog = true
                viewModel.dismissMoveFlowErrorEvent()
            }
            is MainScreenUiEvents.DismissMoveFlowError -> Unit
            is MainScreenUiEvents.ShowFolderFlowError -> {
                showFolderFlowErrorDialog = true
                viewModel.dismissFolderFlowErrorEvent()
            }
            is MainScreenUiEvents.DismissFolderFlowError -> Unit
            is MainScreenUiEvents.ShowRenameFolderDialog -> {
                currentFolderId = event.folderId
                currentFolderName = event.currentName
                showRenameFolderDialog = true
            }
            is MainScreenUiEvents.DismissRenameFolderDialog -> showRenameFolderDialog = false
            is MainScreenUiEvents.ShowDeleteFolderDialog -> {
                currentFolderId = event.folderId
                currentFolderName = event.currentName
                showDeleteFolderDialog = true
            }
            is MainScreenUiEvents.DismissDeleteFolderDialog -> showDeleteFolderDialog = false
            else -> {}
        }

        if (showDeleteDialog) {
            BasicDialog(
                title = stringResource(R.string.main_screen_delete_dialog_title),
                text = stringResource(R.string.main_screen_delete_dialog_text),
                confirmText = stringResource(R.string.main_screen_delete_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_delete_dialog_dismiss_text),
                onConfirm = { viewModel.deleteSelectedNotes() },
                onDismiss = { viewModel.dismissDeleteDialog() }
            )
        }

        if (showNewNoteDialog) {
            InputDialog(
                title = stringResource(R.string.main_screen_create_new_note_dialog_title),
                inputLabel = stringResource(R.string.main_screen_create_new_note_dialog_input_label),
                confirmText = stringResource(R.string.main_screen_create_new_note_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_create_new_note_dialog_dismiss_text),
                onConfirm = { viewModel.createNewNote(it) },
                onDismiss = { viewModel.dismissNewNoteDialog() }
            )
        }

        if (showFilterDialog) {
            FilterDialog(
                searchModel = searchModel,
                onConfirm = {
                    viewModel.onSearchModelUpdate(it)
                    viewModel.dismissFilterDialog()
                },
                onDismiss = { viewModel.dismissFilterDialog() }
            )
        }

        if (showCreateFolderDialog) {
            InputDialog(
                title = stringResource(R.string.main_screen_create_folder_dialog_title),
                inputLabel = stringResource(R.string.main_screen_create_folder_dialog_input_label),
                confirmText = stringResource(R.string.main_screen_create_folder_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_create_folder_dialog_dismiss_text),
                onConfirm = { viewModel.createFolder(it) },
                onDismiss = { viewModel.dismissCreateFolderDialog() }
            )
        }

        if (showMoveToFolderDialog) {
            MoveToFolderDialog(
                folders = viewModel.getFoldersForMoveDialog(),
                onMoveToNoFolder = { viewModel.onMoveToNoFolderConfirmed() },
                onMoveToFolder = { folderId -> viewModel.onMoveToFolderConfirmed(folderId) },
                onCreateFolder = { viewModel.onCreateFolderForMoveClicked() },
                onDismiss = { viewModel.dismissMoveToFolderDialog() }
            )
        }

        if (showCreateFolderForMoveDialog) {
            InputDialog(
                title = stringResource(R.string.main_screen_create_folder_dialog_title),
                inputLabel = stringResource(R.string.main_screen_create_folder_dialog_input_label),
                confirmText = stringResource(R.string.main_screen_create_folder_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_create_folder_dialog_dismiss_text),
                onConfirm = {
                    showCreateFolderForMoveDialog = false
                    viewModel.createFolderForMove(it)
                },
                onDismiss = { viewModel.dismissCreateFolderForMoveDialog() }
            )
        }

        if (showRenameFolderDialog && currentFolderId != null) {
            InputDialog(
                title = stringResource(R.string.main_screen_rename_folder_dialog_title),
                input = currentFolderName,
                inputLabel = stringResource(R.string.main_screen_rename_folder_dialog_input_label),
                confirmText = stringResource(R.string.main_screen_rename_folder_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_rename_folder_dialog_dismiss_text),
                onConfirm = { newName ->
                    val folderId = currentFolderId ?: return@InputDialog
                    viewModel.renameFolder(folderId, newName)
                },
                onDismiss = { viewModel.dismissRenameFolderDialog() }
            )
        }

        if (showDeleteFolderDialog && currentFolderId != null) {
            BasicDialog(
                title = stringResource(R.string.main_screen_delete_folder_dialog_title),
                text = stringResource(R.string.main_screen_delete_folder_dialog_text, currentFolderName),
                confirmText = stringResource(R.string.main_screen_delete_folder_dialog_confirm_text),
                dismissText = stringResource(R.string.main_screen_delete_folder_dialog_dismiss_text),
                onConfirm = {
                    val folderId = currentFolderId ?: return@BasicDialog
                    viewModel.deleteFolder(folderId)
                },
                onDismiss = { viewModel.dismissDeleteFolderDialog() }
            )
        }

        if (showMoveFlowErrorDialog) {
            BasicDialog(
                title = stringResource(R.string.main_screen_error_title),
                text = stringResource(R.string.main_screen_error_move_flow),
                confirmText = stringResource(R.string.main_screen_error_retry),
                dismissText = stringResource(R.string.main_screen_create_new_note_dialog_dismiss_text),
                onConfirm = { showMoveFlowErrorDialog = false },
                onDismiss = { showMoveFlowErrorDialog = false }
            )
        }

        if (showFolderFlowErrorDialog) {
            BasicDialog(
                title = stringResource(R.string.main_screen_error_title),
                text = stringResource(R.string.main_screen_error_folder_flow),
                confirmText = stringResource(R.string.main_screen_error_retry),
                dismissText = stringResource(R.string.main_screen_create_new_note_dialog_dismiss_text),
                onConfirm = { showFolderFlowErrorDialog = false },
                onDismiss = { showFolderFlowErrorDialog = false }
            )
        }
    }
}

@Composable
private fun StateLoading(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading(modifier = Modifier.fillMaxSize())
    }
}

