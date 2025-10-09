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
import androidx.compose.ui.text.style.TextAlign
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

            is UiState.Error -> StateError(
                errorMessage = screenState.errorMessage,
                onRetryPressed = { viewModel.retryOnError() }
            )
        }
    }

    when (events) {
        is NoteScreenUiEvents.ShowTaskRenameDialog -> {
            val taskId = (events as NoteScreenUiEvents.ShowTaskRenameDialog).id
            val currentTitle = (events as NoteScreenUiEvents.ShowTaskRenameDialog).currentTitle

            TaskRenameDialog(
                taskId, currentTitle,
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
            val currentTitle = (events as NoteScreenUiEvents.ShowNoteRenameDialog).currentTitle

            NoteRenameDialog(
                currentTitle,
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
                        text = "Tasks list is empty! \n\n Add new task by pressing '+' in the bottom right corner."
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
    errorMessage: String,
    onRetryPressed: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextBodyMedium(text = "Error! $errorMessage")
        Button(onClick = onRetryPressed) { Text("Retry") }
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
        title = "Rename task",
        input = currentTitle,
        onConfirm = { newTitle -> onConfirm(taskId, newTitle) },
        onDismiss = onDismiss,
        confirmText = "Rename",
        dismissText = "Cancel",
        inputLabel = "New title"
    )
}

@Composable
private fun AddTaskDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    InputDialog(
        title = "Add task",
        onConfirm = { newTitle -> onConfirm(newTitle) },
        onDismiss = onDismiss,
        confirmText = "Add",
        dismissText = "Cancel",
        inputLabel = "New title"
    )
}

@Composable
private fun DeleteNoteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicDialog(
        title = "Delete note",
        text = "Are you sure you want to delete this note?",
        confirmText = "Delete",
        dismissText = "Cancel",
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
        title = "Rename note",
        input = currentTitle,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = "Rename",
        dismissText = "Cancel",
        inputLabel = "New title"
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
        StateError(errorMessage = "Something went wrong!", onRetryPressed = {})
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