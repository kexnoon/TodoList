package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.composables.FolderChipRow

@Composable
fun SortBar(
    modifier: Modifier = Modifier,
    searchModel: SearchModel,
    onSortUpdate: (SearchModel) -> Unit,
    folders: List<Folder> = emptyList(),
    selectedFolderId: Long? = null,
    onFolderSelected: (Long?) -> Unit = {},
    onNewFolderPressed: () -> Unit = {},
    onFolderRenameRequest: (Long) -> Unit = {},
    onFolderDeleteRequest: (Long) -> Unit = {},
    showFolderChips: Boolean = true,
    showSortControls: Boolean = true
) {
    var showSortOrderDropdown by remember { mutableStateOf(false) }
    var showSortByDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showFolderChips) {
            FolderChipRow(
                modifier = Modifier.weight(1f),
                folders = folders,
                selectedFolderId = selectedFolderId,
                onFolderSelected = onFolderSelected,
                onNewFolderPressed = onNewFolderPressed,
                onFolderRenameRequest = onFolderRenameRequest,
                onFolderDeleteRequest = onFolderDeleteRequest
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        if (showSortControls) {
            Row(horizontalArrangement = Arrangement.End) {
                Box {
                    IconButton(onClick = { showSortOrderDropdown = !showSortOrderDropdown}) {
                        Icon(AppIcons.sortOrder, "Sort order")
                    }
                    SortOrderDropdownMenu(
                        expanded = showSortOrderDropdown,
                        onDismissRequest = { showSortOrderDropdown = false },
                        onSortOrderSelected = { sortOrder ->
                            onSortUpdate(searchModel.copy(sortOrder = sortOrder))
                            showSortOrderDropdown = false
                        }
                    )
                }
                Box {
                    IconButton(onClick = { showSortByDropdown = !showSortByDropdown}) {
                        Icon(AppIcons.sortBy, "Sort by")
                    }
                    SortByDropdownMenu(
                        expanded = showSortByDropdown,
                        onDismissRequest = { showSortByDropdown = false },
                        onSortBySelected = { sortBy ->
                            onSortUpdate(searchModel.copy(sortBy = sortBy))
                            showSortByDropdown = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SortOrderDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortOrderSelected: (SortOrder) -> Unit
) {
    if (!expanded) return
    DropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_asc)) },
            onClick = { onSortOrderSelected(SortOrder.ASC) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_desc)) },
            onClick = { onSortOrderSelected(SortOrder.DESC) }
        )
    }
}

@Composable
private fun SortByDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    onSortBySelected: (SortBy) -> Unit
) {
    if (!expanded) return
    DropdownMenu(
        offset = DpOffset(x = -16.dp, y = 0.dp),
        expanded = true,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_title)) },
            onClick = { onSortBySelected(SortBy.TITLE) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_status)) },
            onClick = { onSortBySelected(SortBy.STATUS) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_created_at)) },
            onClick = { onSortBySelected(SortBy.CREATED_AT) }
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.sort_bar_updated_at)) },
            onClick = { onSortBySelected(SortBy.UPDATED_AT) }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SortBar_Preview() {
    TodoListTheme {
        SortBar(
            searchModel = SearchModel(),
            onSortUpdate = {},
            folders = emptyList()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SortBar_WithChips_Preview() {
    TodoListTheme {
        SortBar(
            searchModel = SearchModel(),
            onSortUpdate = {},
            folders = listOf(
                Folder(id = 1L, name = "Work", lastUpdatedTimestamp = "2024-01-01T00:00:00Z"),
                Folder(id = 2L, name = "Personal", lastUpdatedTimestamp = "2024-01-01T00:00:00Z")
            ),
            selectedFolderId = null
        )
    }
}
