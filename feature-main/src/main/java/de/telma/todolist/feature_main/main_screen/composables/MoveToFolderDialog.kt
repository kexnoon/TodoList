package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R

@Composable
internal fun MoveToFolderDialog(
    folders: List<Folder>,
    onMoveToNoFolder: () -> Unit,
    onMoveToFolder: (Long) -> Unit,
    onCreateFolder: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(text = stringResource(R.string.main_screen_move_to_folder_dialog_title)) },
        text = {
            Column {
                TextButton(
                    onClick = onMoveToNoFolder
                ) {
                    Text(text = stringResource(R.string.main_screen_move_to_folder_dialog_no_folder))
                }
                folders.forEach { folder ->
                    TextButton(
                        onClick = { onMoveToFolder(folder.id) }
                    ) {
                        Text(text = folder.name)
                    }
                }
                TextButton(
                    onClick = onCreateFolder
                ) {
                    Text(text = stringResource(R.string.main_screen_move_to_folder_dialog_new_folder))
                }
            }
        },
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.main_screen_move_to_folder_dialog_dismiss_text))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MoveToFolderDialog_Preview() {
    TodoListTheme {
        MoveToFolderDialog(
            folders = listOf(
                Folder(id = 1L, name = "Work", lastUpdatedTimestamp = "2026-01-01T12:00:00Z"),
                Folder(id = 2L, name = "Personal", lastUpdatedTimestamp = "2026-01-02T12:00:00Z")
            ),
            onMoveToNoFolder = {},
            onMoveToFolder = {},
            onCreateFolder = {},
            onDismiss = {}
        )
    }
}
