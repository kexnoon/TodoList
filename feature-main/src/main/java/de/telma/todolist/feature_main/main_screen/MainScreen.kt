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
                onClearSelectionClick = { viewModel.onClearSelectionClicked() },
                onNewNoteClick = { viewModel.onNewNoteClicked() },
                onItemClick = { viewModel.toDetailsScreen(it) },
                onItemSelected = { id, isSelected -> viewModel.onNoteSelected(id, isSelected) },
                onSearchInput = { viewModel.onSearchQueryInput(it) },
                onSearchClear = { viewModel.onClearSearchClicked() },
                onSearchFilterClicked = { viewModel.showFilterDialog() },
                onSortUpdate = { viewModel.onSearchModelUpdate(it) },
            )

            is UiState.Error -> StateError(
                modifier = Modifier.fillMaxSize(),
                error = (uiState as UiState.Error<MainScreenUiErrors>).uiError,
                onRetryPressed = { viewModel.retryOnError() }
            )
        }

        when (uiEvents) {
            is MainScreenUiEvents.ShowDeleteDialog -> showDeleteDialog = true
            is MainScreenUiEvents.DismissDeleteDialog -> showDeleteDialog = false
            is MainScreenUiEvents.ShowCreateNewNoteDialog -> showNewNoteDialog = true
            is MainScreenUiEvents.DismissCreateNewNoteDialog -> showNewNoteDialog = false
            is MainScreenUiEvents.ShowFilterDialog -> showFilterDialog = true
            is MainScreenUiEvents.DismissFilterDialog -> showFilterDialog = false
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

