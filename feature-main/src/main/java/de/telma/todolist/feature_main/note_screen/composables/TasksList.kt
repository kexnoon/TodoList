package de.telma.todolist.feature_main.note_screen.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.note_screen.models.TaskItemModel

@Composable
fun TasksList(
    modifier: Modifier = Modifier,
    tasks: List<TaskItemModel>,
    onRenameTaskPressed: (taskId: Long) -> Unit = {},
    onDeleteTaskPressed: (taskId: Long) -> Unit = {},
    onItemClicked: (taskId: Long) -> Unit = {}
) {
    LazyColumn(modifier = modifier) {
        items(tasks.size) {
            TaskItem(
                model = tasks[it],
                onRenamePressed = onRenameTaskPressed,
                onDeletePressed = onDeleteTaskPressed,
                onItemClicked = onItemClicked
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TasksList_Preview() {
    TodoListTheme {
        val task = TaskItemModel(
            id = 0,
            title = "Item",
            isCompleted = false
        )

        var tasksList by rememberSaveable { mutableStateOf(List(10) { index ->
            task.copy(id = index.toLong(),
                title = "Item $index") })
        }

        TasksList(
            modifier = Modifier.fillMaxWidth().wrapContentSize(),
            tasks = tasksList,
            onItemClicked = { id ->
                tasksList = tasksList.map {
                    if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                }
            }
        )
    }
}