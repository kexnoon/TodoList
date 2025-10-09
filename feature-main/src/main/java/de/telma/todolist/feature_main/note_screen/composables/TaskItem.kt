package de.telma.todolist.feature_main.note_screen.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.TextBodyLarge
import de.telma.todolist.core_ui.theme.AppColors
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.note_screen.models.TaskItemModel

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    model: TaskItemModel,
    onRenamePressed: (taskId: Long) -> Unit = {},
    onDeletePressed: (taskId: Long) -> Unit = {},
    onItemClicked: (taskId: Long) -> Unit = {}
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = { onItemClicked(model.id) }),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Checkbox(checked = model.isCompleted, onCheckedChange = { onItemClicked(model.id) })
        if (model.isCompleted)
            TextBodyLarge(
                modifier = Modifier.weight(1f),
                color = AppColors.itemTitleCompleted,
                textDecoration = TextDecoration.LineThrough,
                text = model.title
            )
        else
            TextBodyLarge(
                modifier = Modifier.weight(1f),
                color = AppColors.itemTitleInProgress,
                textDecoration = TextDecoration.None,
                text = model.title
            )
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy((-10).dp)
        ) {
            IconButton(onClick = { onRenamePressed(model.id) }) {
                Icon(
                    imageVector = AppIcons.edit,
                    contentDescription = "Rename task"
                )
            }
            IconButton(onClick = { onDeletePressed(model.id) }) {
                Icon(
                    imageVector = AppIcons.delete,
                    contentDescription = "Delete task"
                )
            }

        }
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItem_Preview_InProgress() {
    TodoListTheme {
        val model = TaskItemModel(
            id = 0,
            title = "Item in progress",
            isCompleted = false
        )
        TaskItem(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            model = model
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItem_Preview_Completed() {
    TodoListTheme {
        val model = TaskItemModel(
            id = 0,
            title = "Item completed",
            isCompleted = true
        )
        TaskItem(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            model = model
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItem_Preview_Playground() {
    TodoListTheme {
        var model by remember {
            mutableStateOf(
                TaskItemModel(
                    id = 0,
                    title = "Playground item",
                    isCompleted = false
                )
            )
        }
        TaskItem(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            model = model,
            onItemClicked = { model = model.copy(isCompleted = !model.isCompleted) }
        )
    }
}