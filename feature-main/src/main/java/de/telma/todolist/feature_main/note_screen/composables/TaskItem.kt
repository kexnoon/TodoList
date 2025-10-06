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

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    title: String,
    isCompleted: Boolean,
    onRenamePressed: () -> Unit = {},
    onDeletePressed: () -> Unit = {},
    onItemClicked: () -> Unit = {}
) {
    Row(modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onItemClicked),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Checkbox(checked = isCompleted, onCheckedChange = { onItemClicked() })
        if (isCompleted)
            TextBodyLarge(
                modifier = Modifier.weight(1f),
                color =  AppColors.itemTitleCompleted,
                textDecoration = TextDecoration.LineThrough,
                text = title
            )
        else
            TextBodyLarge(
                modifier = Modifier.weight(1f),
                color =  AppColors.itemTitleInProgress,
                textDecoration = TextDecoration.None,
                text = title
            )
        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy((-10).dp)
        ) {
            IconButton(onClick = onRenamePressed) {
                Icon(
                    imageVector = AppIcons.edit,
                    contentDescription = "Rename task"
                )
            }
            IconButton(onClick = onDeletePressed) {
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
        val title = "Item in progress"
        TaskItem(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            title = title,
            isCompleted = false
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItem_Preview_Completed() {
    TodoListTheme {
        val title = "Completed item"
        TaskItem(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            title = title,
            isCompleted = true
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItem_Preview_Playground() {
    TodoListTheme {

        val title = "Playground item"
        var isCompleted by remember { mutableStateOf(false) }
        TaskItem(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            title = title,
            isCompleted = isCompleted,
            onItemClicked = { isCompleted = !isCompleted }
        )
    }
}