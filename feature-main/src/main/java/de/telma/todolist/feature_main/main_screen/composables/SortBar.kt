package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.model.SortBy
import de.telma.todolist.component_notes.model.SortOrder
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R

@Composable
fun SortBar(
    searchModel: SearchModel,
    onSortUpdate: (SearchModel) -> Unit
) {
    var showSortOrderDropdown by remember { mutableStateOf(false) }
    var showSortByDropdown by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Box {
            IconButton(onClick = { showSortOrderDropdown = !showSortOrderDropdown}) {
                Icon(AppIcons.sortOrder, "Sort order")
            }
            if (showSortOrderDropdown) {
                DropdownMenu(
                    expanded = showSortOrderDropdown,
                    onDismissRequest = { showSortOrderDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_asc)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortOrder = SortOrder.ASC))
                            showSortOrderDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_desc)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortOrder = SortOrder.DESC))
                            showSortOrderDropdown = false
                        }
                    )
                }
            }
        }
        Box {
            IconButton(onClick = { showSortByDropdown = !showSortByDropdown}) {
                Icon(AppIcons.sortBy, "Sort by")
            }
            if (showSortByDropdown) {
                DropdownMenu(
                    offset = DpOffset(x = -16.dp, y = 0.dp),
                    expanded = showSortByDropdown,
                    onDismissRequest = { showSortByDropdown = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_title)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortBy = SortBy.TITLE))
                            showSortByDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_status)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortBy = SortBy.STATUS))
                            showSortByDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_created_at)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortBy = SortBy.CREATED_AT))
                            showSortByDropdown = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.sort_bar_updated_at)) },
                        onClick = {
                            onSortUpdate(searchModel.copy(sortBy = SortBy.UPDATED_AT))
                            showSortByDropdown = false
                        }
                    )
                }
            }
        }

    }


}

@Composable
@Preview(showBackground = true)
fun SortBar_Preview() {
    TodoListTheme {
        SortBar(
            searchModel = SearchModel(),
            onSortUpdate = {}
        )
    }
}