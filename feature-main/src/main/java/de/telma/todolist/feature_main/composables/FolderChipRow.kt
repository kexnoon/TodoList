package de.telma.todolist.feature_main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.core_ui.composables.FilterChip
import de.telma.todolist.core_ui.composables.FilterChipModel
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R

@Composable
internal fun FolderChipRow(
    modifier: Modifier = Modifier,
    folders: List<Folder>,
    selectedFolderId: Long?,
    onFolderSelected: (Long?) -> Unit,
    onNewFolderPressed: () -> Unit,
    onFolderRenameRequest: (Long) -> Unit,
    onFolderDeleteRequest: (Long) -> Unit
) {
    var expandedMenuFolderId by remember { mutableStateOf<Long?>(null) }
    val allNotesText = stringResource(R.string.main_screen_folder_chip_all_notes)
    val newFolderText = stringResource(R.string.main_screen_folder_chip_new_folder)

    LazyRow(
        modifier = modifier
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val allModelsModel = FilterChipModel(
            text = allNotesText,
            selected = selectedFolderId == null,
            onClick = { onFolderSelected(null) }
        )
        item(key = "all-notes") {
            FilterChip(model = allModelsModel)
        }
        items(folders, key = { folder -> folder.id }) { folder ->
            Box {
                val chipModel = FilterChipModel(
                    text = folder.name,
                    selected = selectedFolderId == folder.id,
                    icon = AppIcons.folder,
                    iconContentDescription = folder.name,
                    onClick = { onFolderSelected(folder.id) },
                    onLongClick = { expandedMenuFolderId = folder.id }
                )

                FilterChip(model = chipModel)
                DropdownMenu(
                    expanded = expandedMenuFolderId == folder.id,
                    onDismissRequest = { expandedMenuFolderId = null }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.main_screen_folder_actions_dialog_rename_text))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.edit,
                                contentDescription = stringResource(R.string.main_screen_folder_actions_dialog_rename_text)
                            )
                        },
                        onClick = {
                            expandedMenuFolderId = null
                            onFolderRenameRequest(folder.id)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(R.string.main_screen_folder_actions_dialog_delete_text))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.delete,
                                contentDescription = stringResource(R.string.main_screen_folder_actions_dialog_delete_text)
                            )
                        },
                        onClick = {
                            expandedMenuFolderId = null
                            onFolderDeleteRequest(folder.id)
                        }
                    )
                }
            }
        }
        item(key = "new-folder") {
            val chipModel = FilterChipModel(
                text = newFolderText,
                selected = false,
                icon = AppIcons.add,
                iconContentDescription = newFolderText,
                onClick = onNewFolderPressed
            )
            FilterChip(model = chipModel)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FolderChipRow_Preview() {
    TodoListTheme {
        Surface {
            FolderChipRow(
                folders = listOf(
                    Folder(id = 1L, name = "Work", lastUpdatedTimestamp = "2024-01-01T00:00:00Z"),
                    Folder(id = 2L, name = "Personal", lastUpdatedTimestamp = "2024-01-01T00:00:00Z")
                ),
                selectedFolderId = null,
                onFolderSelected = {},
                onNewFolderPressed = {},
                onFolderRenameRequest = {},
                onFolderDeleteRequest = {}
            )
        }
    }
}
