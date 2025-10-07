package de.telma.todolist.feature_main.note_screen

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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.BasicDialog
import de.telma.todolist.core_ui.composables.InputDialog
import de.telma.todolist.core_ui.composables.TextBodyMedium
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.note_screen.composables.NoteScreenAppBar
import de.telma.todolist.feature_main.note_screen.composables.TasksList
import de.telma.todolist.feature_main.note_screen.models.NoteScreenAppBarModel
import de.telma.todolist.feature_main.note_screen.models.TaskItemModel

@Composable
fun NoteScreen(
    viewModel: NoteScreenViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val events by viewModel.uiEvents.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is UiState.Loading -> StateLoading()
            is UiState.Result<NoteScreenState> -> StateResult(
                screenState = (state as UiState.Result<NoteScreenState>).data,
                onBackPressed = { viewModel.onBackPressed() },
                onRenameNotePressed = { viewModel.onRenameNotePressed() },
                onDeleteNotePressed = { viewModel.onDeleteNotePressed() },
                onItemClicked = { taskId -> viewModel.updateTaskStatus(taskId) },
                onTaskRenamePressed = { taskId -> viewModel.onTaskRenamePressed(taskId) },
                onDeleteTaskPressed = { taskId -> viewModel.deleteTask(taskId) },
                onAddTaskPressed = { viewModel.onAddTaskPressed() }
            )
            is UiState.Error -> StateError(
                errorMessage = (state as UiState.Error).throwable.message ?: "Something went wrong!",
                onRetryPressed = { viewModel.retryOnError() }
            )
        }

        when (events) {
            is NoteScreenUiEvents.ShowTaskRenameDialog -> {
                val taskId = (events as NoteScreenUiEvents.ShowTaskRenameDialog).id
                val currentTitle = (events as NoteScreenUiEvents.ShowTaskRenameDialog).currentTitle

                InputDialog(
                    title = "Rename task",
                    input = currentTitle,
                    onConfirm = { newTitle -> viewModel.renameTask(taskId, newTitle) },
                    onDismiss = { viewModel.dismissTaskRenameDialog() },
                    confirmText = "Rename",
                    dismissText = "Cancel",
                    inputLabel = "New title"
                )
            }
            is NoteScreenUiEvents.ShowAddTaskDialog -> {
                InputDialog(
                    title = "Add task",
                    onConfirm = { newTitle -> viewModel.addTask(newTitle) },
                    onDismiss = { viewModel.dismissAddTaskDialog() },
                    confirmText = "Add",
                    dismissText = "Cancel",
                    inputLabel = "New title"
                )
            }
            is NoteScreenUiEvents.ShowDeleteNoteDialog -> {
                BasicDialog(
                    title = "Delete note",
                    text = "Are you sure you want to delete this note?",
                    confirmText = "Delete",
                    dismissText = "Cancel",
                    onConfirm = { viewModel.deleteNote() },
                    onDismiss = { viewModel.dismissDeleteNoteDialog() }
                )
            }
            is NoteScreenUiEvents.ShowNoteRenameDialog -> {
                val currentTitle = (events as NoteScreenUiEvents.ShowNoteRenameDialog).currentTitle

                InputDialog(
                    title = "Rename note",
                    input = currentTitle,
                    onConfirm = { newTitle -> viewModel.renameNote(newTitle) },
                    onDismiss = { viewModel.dismissNoteRenameDialog() },
                    confirmText = "Rename",
                    dismissText = "Cancel",
                    inputLabel = "New title"
                )
            }
            is NoteScreenUiEvents.DismissTaskRenameDialog -> {
                viewModel.dismissTaskRenameDialog()
            }
            else -> {}
        }
    }

}

@Composable
fun StateResult(
    modifier: Modifier = Modifier,
    screenState: NoteScreenState,
    onBackPressed: () -> Unit = {},
    onRenameNotePressed: () -> Unit = {},
    onDeleteNotePressed: () -> Unit = {},
    onItemClicked: (taskId: Long) -> Unit = {},
    onTaskRenamePressed: (taskId: Long) -> Unit = {},
    onDeleteTaskPressed: (taskId: Long) -> Unit = {},
    onAddTaskPressed: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteScreenAppBar(
                modifier = Modifier.fillMaxWidth(),
                model = screenState.appBar,
                onBackPressed = { onBackPressed() },
                onRenamePressed = { onRenameNotePressed() },
                onDeletePressed = { onDeleteNotePressed() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp),
                onClick = { onAddTaskPressed() }
            ) {
                Icon(AppIcons.add, "")
            }
        },
        content = { paddingValues ->
            TasksList(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                tasks = screenState.tasks,
                onItemClicked = { taskId ->  onItemClicked(taskId)},
                onRenameTaskPressed = { taskId -> onTaskRenamePressed(taskId) },
                onDeleteTaskPressed = { taskId -> onDeleteTaskPressed(taskId) }
            )
        }
    )
}

@Composable
fun StateLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

@Composable
fun StateError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onRetryPressed: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextBodyMedium(text = "Error! $errorMessage")
        Button(onClick = onRetryPressed) { Text("Retry") }
    }
}

@Composable
@Preview(showBackground = true)
fun StateResult_Preview() {
    TodoListTheme {
        val noteId = 0L
        val appBarState = NoteScreenAppBarModel(
            noteId = noteId,
            title = "Note",
            isComplete = false
        )
        val task = TaskItemModel(
            id = 0,
            title = "Item",
            isCompleted = false
        )

        var tasksList by rememberSaveable {
            mutableStateOf(List(10) { index ->
                task.copy(
                    id = index.toLong(),
                    title = "Item $index"
                )
            })
        }

        val state by rememberSaveable {
            mutableStateOf(
                NoteScreenState(
                    noteId, appBarState, tasksList
                )
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            StateResult(
                modifier = Modifier.fillMaxSize(),
                screenState = state
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading()
    }
}

@Composable
@Preview(showBackground = true)
fun StateError_Preview() {
    TodoListTheme {
        StateError(errorMessage = "Something went wrong!")
    }
}