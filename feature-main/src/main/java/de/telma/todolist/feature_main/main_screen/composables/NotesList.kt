package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.core_ui.theme.TodoListTheme
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.models.NotesListItemState

@Composable
fun NotesList(
    modifier: Modifier = Modifier,
    items: List<NotesListItemModel>,
    isSelectionMode: Boolean,
    onClick: (Long) -> Unit = {},
    onLongClick: (Long) -> Unit = {},
    onItemSelected: (Long, Boolean) -> Unit = { _, _ -> }
) {
    LazyColumn(modifier = modifier) {
        items(items.size) { index ->
            NotesListItem(
                model = items[index],
                isSelectionMode = isSelectionMode,
                onClick = onClick,
                onLongClick = onLongClick,
                onCheckedChange = { id, isSelected -> onItemSelected(id, isSelected) }
            )
            if (index < items.size - 1) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun NotesList_ShowItems_Preview() {
    TodoListTheme {
        val item = NotesListItemModel(
            id = 0L,
            title = "Test Note",
            status = NotesListItemState.IN_PROGRESS,
            numberOfTasks = 7,
            lastUpdatedTimestamp = "2023-01-01T10:00:00Z"
        )

        var list by rememberSaveable { mutableStateOf(List(10) { index -> item.copy(id = index.toLong()) }) }
        var isSelectionMode by rememberSaveable { mutableStateOf(false) }

        Column {
            NotesList(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 16.dp),
                items = list,
                isSelectionMode = isSelectionMode,
                onLongClick = { if (!isSelectionMode) isSelectionMode = true },
                onItemSelected = { id, isSelected ->
                    list = list.map { if (it.id == id) it.copy(isSelected = isSelected) else it }
                }
            )

            Button(onClick = { isSelectionMode = false }) { Text("Clear Selection") }
        }
    }
}