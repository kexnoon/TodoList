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

    val scaffoldActions = ScaffoldActions(
        onRenamePressed = { viewModel.onRenameNotePressed() },
        onDeletePressed = { viewModel.onDeleteNotePressed() },
        onBackPressed = { viewModel.onBackPressed() },
        onAddTaskPressed = { viewModel.onAddTaskPressed() }
    )

    val itemActions = ItemActions(
        onItemClicked = { taskId -> viewModel.updateTaskStatus(taskId) },
        onRenamePressed = { taskId -> viewModel.onTaskRenamePressed(taskId) },
        onDeletePressed = { taskId -> viewModel.deleteTask(taskId) }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val screenState = state) {
            is UiState.Loading -> StateLoading()
            is UiState.Result<NoteScreenState> -> StateResult(
                screenState = screenState.data,
                scaffoldActions = scaffoldActions,
                itemActions = itemActions
            )

            is UiState.Error<NoteScreenUiErrors> -> StateError(
                error = screenState.uiError,
                onRetryPressed = { viewModel.retryOnError() }
            )
        }
    }

    when (val event = events) {
        is NoteScreenUiEvents.ShowTaskRenameDialog -> {
            TaskRenameDialog(
                event.id, event.currentTitle,
                onConfirm = { taskId, newTitle -> viewModel.renameTask(taskId, newTitle) },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        is NoteScreenUiEvents.ShowAddTaskDialog -> {
            AddTaskDialog(
                onConfirm = { title -> viewModel.addTask(title) },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        is NoteScreenUiEvents.ShowDeleteNoteDialog -> {
            DeleteNoteDialog(
                onConfirm = { viewModel.deleteNote() },
                onDismiss = { viewModel.dismissDialog() }
            )
        }

        is NoteScreenUiEvents.ShowNoteRenameDialog -> {
            NoteRenameDialog(
                event.currentTitle,
                onConfirm = { newTitle -> viewModel.renameNote(newTitle) },
                onDismiss = { viewModel.dismissDialog() })
        }

        else -> {}
    }
}

@Composable
private fun StateResult(
    modifier: Modifier = Modifier,
    screenState: NoteScreenState,
    scaffoldActions: ScaffoldActions,
    itemActions: ItemActions
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            NoteScreenAppBar(
                modifier = Modifier.fillMaxWidth(),
                model = screenState.appBar,
                onBackPressed = { scaffoldActions.onBackPressed.invoke() },
                onRenamePressed = { scaffoldActions.onRenamePressed.invoke() },
                onDeletePressed = { scaffoldActions.onDeletePressed.invoke() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp),
                onClick = scaffoldActions.onAddTaskPressed
            ) {
                Icon(AppIcons.add, "Add new task")
            }
        },
        content = { paddingValues ->
            val tasks = screenState.tasks

            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    TextBodyMedium(
                        modifier = Modifier.padding(all = 16.dp),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.note_screen_placeholder)
                    )
                }
            } else {
                TasksList(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    tasks = screenState.tasks,
                    onItemClicked = { taskId -> itemActions.onItemClicked.invoke(taskId) },
                    onRenameTaskPressed = { taskId -> itemActions.onRenamePressed.invoke(taskId) },
                    onDeleteTaskPressed = { taskId -> itemActions.onDeletePressed.invoke(taskId) }
                )
            }

        }
    )
}

@Composable
private fun StateLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

@Composable
private fun StateError(
    modifier: Modifier = Modifier,
    error: NoteScreenUiErrors,
    onRetryPressed: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextBodyMedium(text = stringResource(R.string.note_screen_error_title))
        TextBodyMedium(text = errorHandler(error = error))
        Button(onClick = onRetryPressed) { Text(stringResource(R.string.note_screen_error_retry)) }
    }
}


@Composable
private fun errorHandler(error: NoteScreenUiErrors): String {
    return when (error) {
        is NoteScreenUiErrors.NoteNotFound ->
            stringResource(R.string.note_screen_error_note_not_found, error.noteId)
        is NoteScreenUiErrors.TaskNotFound ->
            stringResource(R.string.note_screen_error_task_not_found, error.taskId)
        is NoteScreenUiErrors.FailedToUpdateTaskStatus ->
            stringResource(R.string.note_screen_error_failed_to_update_task_status, error.taskId)
        is NoteScreenUiErrors.FailedToDeleteNote ->
            stringResource(R.string.note_screen_error_failed_to_delete_note, error.noteId)
        is NoteScreenUiErrors.FailedToRenameNote ->
            stringResource(R.string.note_screen_error_failed_to_rename_note, error.noteId)
        is NoteScreenUiErrors.FailedToCreateNewTask ->
            stringResource(R.string.note_screen_error_failed_to_create_new_task)
        is NoteScreenUiErrors.FailedToRenameTask ->
            stringResource(R.string.note_screen_error_failed_to_rename_task, error.taskId)
        is NoteScreenUiErrors.FailedToDeleteTask ->
            stringResource(R.string.note_screen_error_failed_to_delete_task, error.taskId)
        is NoteScreenUiErrors.FailedToSyncNoteStatus ->
            stringResource(R.string.note_screen_error_failed_to_sync_note_status, error.noteId)
    }
}

@Composable
private fun TaskRenameDialog(
    taskId: Long,
    currentTitle: String,
    onConfirm: (Long, String) -> Unit,
    onDismiss: () -> Unit
) {
    InputDialog(
        title = stringResource(R.string.note_screen_task_rename_title),
        input = currentTitle,
        onConfirm = { newTitle -> onConfirm(taskId, newTitle) },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.note_screen_task_rename_confirm),
        dismissText = stringResource(R.string.note_screen_task_rename_dismiss),
        inputLabel = stringResource(R.string.note_screen_task_rename_input_label)
    )
}

@Composable
private fun AddTaskDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    InputDialog(
        title = stringResource(R.string.note_screen_add_task_title),
        onConfirm = { newTitle -> onConfirm(newTitle) },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.note_screen_add_task_confirm),
        dismissText = stringResource(R.string.note_screen_add_task_dismiss),
        inputLabel = stringResource(R.string.note_screen_add_task_input_label)
    )
}

@Composable
private fun DeleteNoteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicDialog(
        title = stringResource(R.string.note_screen_delete_note_title),
        text = stringResource(R.string.note_screen_delete_note_text),
        confirmText = stringResource(R.string.note_screen_delete_note_confirm),
        dismissText = stringResource(R.string.note_screen_delete_note_dismiss),
        onConfirm = onConfirm,
        onDismiss = onDismiss
    )
}

@Composable
private fun NoteRenameDialog(
    currentTitle: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    InputDialog(
        title = stringResource(R.string.note_screen_rename_note_title),
        input = currentTitle,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.note_screen_rename_note_confirm),
        dismissText = stringResource(R.string.note_screen_rename_note_dismiss),
        inputLabel = stringResource(R.string.note_screen_rename_note_input_label)
    )
}

@Composable
@Preview(showBackground = true)
private fun StateResult_Default_Preview() {
    TodoListTheme {
        val noteId = 0L
        val appBarState =
            NoteScreenAppBarModel(noteId = noteId, title = "Test Note", isComplete = false)
        val task = TaskItemModel(id = 0, title = "Item", isCompleted = false)

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

        val scaffoldActions = ScaffoldActions()
        val itemActions = ItemActions()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            StateResult(
                modifier = Modifier.fillMaxSize(),
                screenState = state,
                scaffoldActions,
                itemActions
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StateResult_NoTasks_Preview() {
    TodoListTheme {
        val noteId = 0L
        val appBarState =
            NoteScreenAppBarModel(noteId = noteId, title = "Empty note", isComplete = false)

        val state by rememberSaveable {
            mutableStateOf(
                NoteScreenState(
                    noteId, appBarState, listOf()
                )
            )
        }

        val scaffoldActions = ScaffoldActions()
        val itemActions = ItemActions()

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            StateResult(
                modifier = Modifier.fillMaxSize(),
                screenState = state,
                scaffoldActions,
                itemActions
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading()
    }
}

@Composable
@Preview(showBackground = true)
private fun StateError_Preview() {
    TodoListTheme {
        StateError(error = NoteScreenUiErrors.FailedToCreateNewTask, onRetryPressed = {})
    }
}

private data class ScaffoldActions(
    val onRenamePressed: () -> Unit = {},
    val onDeletePressed: () -> Unit = {},
    val onBackPressed: () -> Unit = {},
    val onAddTaskPressed: () -> Unit = {}
)

private data class ItemActions(
    val onItemClicked: (taskId: Long) -> Unit = {},
    val onRenamePressed: (taskId: Long) -> Unit = {},
    val onDeletePressed: (taskId: Long) -> Unit = {}
)
