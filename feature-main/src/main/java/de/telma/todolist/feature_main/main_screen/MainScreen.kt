package de.telma.todolist.feature_main.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.BasicDialog
import de.telma.todolist.core_ui.composables.InputDialog
import de.telma.todolist.core_ui.composables.TextBodyMedium
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.main_screen.composables.MainScreenAppBar
import de.telma.todolist.feature_main.main_screen.composables.NotesList
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.models.NotesListItemState

@Composable
internal fun MainScreen(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiEvents by viewModel.uiEvents.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showNewNoteDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val result = uiState) {
            is UiState.Loading -> StateLoading(modifier = Modifier.fillMaxSize())
            is UiState.Result<MainScreenState> -> StateResult(
                modifier = Modifier.fillMaxSize(),
                result = result.data,
                onDeleteClick = { viewModel.onDeleteClicked() },
                onClearSelectionClick = { viewModel.onClearSelectionClicked() },
                onNewNoteClick = { viewModel.onNewNoteClicked() },
                onItemClick = { viewModel.toDetailsScreen(it) },
                onItemSelected = { id, isSelected -> viewModel.onNoteSelected(id, isSelected) }
            )

            is UiState.Error -> StateError(
                modifier = Modifier.fillMaxSize(),
                error = (uiState as UiState.Error<MainScreenUiErrors>).uiError,
                onRetryPressed = { viewModel.retryOnError() }
            )
        }

        when (uiEvents) {
            is MainScreenUiEvents.ShowDeleteDialog -> showDeleteDialog = true
            is MainScreenUiEvents.ShowCreateNewNoteDialog -> showNewNoteDialog = true
            is MainScreenUiEvents.DismissDeleteDialog -> showDeleteDialog = false
            is MainScreenUiEvents.DismissCreateNewNoteDialog -> showNewNoteDialog = false
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StateResult(
    modifier: Modifier = Modifier,
    result: MainScreenState,
    onDeleteClick: () -> Unit = {},
    onClearSelectionClick: () -> Unit = {},
    onNewNoteClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    onItemSelected: (Long, Boolean) -> Unit = { _, _ -> }
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            MainScreenAppBar(
                modifier = Modifier.fillMaxWidth(),
                isSelectionMode = result.isSelectionMode,
                selectionCount = result.selectedNotesCount,
                onDeleteClick = { onDeleteClick.invoke() },
                onClearSelectionClick = { onClearSelectionClick.invoke() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp),
                onClick = { onNewNoteClick.invoke() }
            ) {
                Icon(AppIcons.add, "Add new Note")
            }
        },
        content = { paddingValues ->
            if (result.notes.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TextBodyMedium(
                        modifier = Modifier.padding(all = 16.dp),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.main_screen_placeholder)
                    )
                }
            }
            NotesList(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                items = result.notes,
                isSelectionMode = result.isSelectionMode,
                onClick = { id -> onItemClick.invoke(id) },
                onLongClick = { id -> onItemSelected.invoke(id, true) },
                onItemSelected = { id, isSelected -> onItemSelected(id, isSelected) }
            )
        }
    )
}

@Composable
private fun StateError(
    modifier: Modifier = Modifier,
    error: MainScreenUiErrors,
    onRetryPressed: () -> Unit = {}
) {
    TodoListTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextBodyMedium(text = stringResource(R.string.main_screen_error_title))
            TextBodyMedium(text = errorHandler(error))
            Button(onClick = onRetryPressed) {
                Text(stringResource(R.string.main_screen_error_retry))
            }
        }
    }
}


@Composable
private fun errorHandler(error: MainScreenUiErrors): String {
    return when (error) {
        is MainScreenUiErrors.FailedToCreateNewNote ->
            stringResource(R.string.main_screen_error_failed_to_create_new_note)
        is MainScreenUiErrors.FailedToDeleteNotes ->
            stringResource(R.string.main_screen_error_failed_to_delete_notes)
    }
}

@Preview(showBackground = true)
@Composable
private fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun StateResult_Default_Preview() {
    TodoListTheme {
        val note = NotesListItemModel(
            id = 0L,
            title = "Test Note",
            status = NotesListItemState.IN_PROGRESS,
            lastUpdatedTimestamp = "12.01.2023",
            numberOfTasks = 3
        )

        val notes = List(10) { note.copy(id = it.toLong(), title = "Test Note $it") }
        val screenState by remember { mutableStateOf(MainScreenState(notes = notes)) }

        StateResult(modifier = Modifier.fillMaxSize(), result = screenState)
    }

}

@Preview(showBackground = true)
@Composable
private fun StateResult_EmptyNotes_Preview() {
    TodoListTheme {
        val screenState by remember { mutableStateOf(MainScreenState(notes = listOf())) }
        StateResult(modifier = Modifier.fillMaxSize(), result = screenState)
    }
}

@Preview(showBackground = true)
@Composable
private fun StateError_Preview() {
    TodoListTheme {
        StateError(
            modifier = Modifier.fillMaxSize(),
            error = MainScreenUiErrors.FailedToCreateNewNote
        )
    }
}
