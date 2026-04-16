package de.telma.todolist.feature_main.main_screen.states

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.core_ui.composables.TextBodyMedium
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.main_screen.MainScreenState
import de.telma.todolist.feature_main.main_screen.composables.MainScreenAppBar
import de.telma.todolist.feature_main.main_screen.composables.MainScreenAppBarState
import de.telma.todolist.feature_main.main_screen.composables.NotesList
import de.telma.todolist.feature_main.main_screen.composables.SearchBar
import de.telma.todolist.feature_main.main_screen.composables.SearchBarState
import de.telma.todolist.feature_main.main_screen.composables.SortBar
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.models.NotesListItemState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StateResult(
    modifier: Modifier = Modifier,
    result: MainScreenState,
    searchModel: SearchModel,
    onDeleteClick: () -> Unit = {},
    onClearSelectionClick: () -> Unit = {},
    onNewNoteClick: () -> Unit = {},
    onItemClick: (Long) -> Unit = {},
    onItemSelected: (Long, Boolean) -> Unit = { _, _ -> },
    onSearchInput: (String) -> Unit = {},
    onSearchClear: () -> Unit = {},
    onSearchFilterClicked: () -> Unit = {},
    onSortUpdate: (SearchModel) -> Unit = {},
    onFolderSelected: (Long?) -> Unit = {},
    onNewFolderPressed: () -> Unit = {},
    onFolderRenameRequest: (Long) -> Unit = {},
    onFolderDeleteRequest: (Long) -> Unit = {}
) {
    val appBarState = remember(result) {
        when {
            result.isSelectionMode -> MainScreenAppBarState.Selection(result.selectedNotesCount)
            searchModel.query.isNullOrEmpty() -> MainScreenAppBarState.Default
            else -> MainScreenAppBarState.Search(result.searchCounter ?: 0)
        }
    }
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {
                MainScreenAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    state = appBarState,
                    onDeleteClick = onDeleteClick,
                    onClearSelectionClick = onClearSelectionClick
                )
                SearchBar(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    input = searchModel.query ?: "",
                    state = if (searchModel.query.isNullOrEmpty()) SearchBarState.DEFAULT else SearchBarState.ACTIVE,
                    onInput = onSearchInput,
                    onClearClicked = onSearchClear,
                    onFilterClicked = onSearchFilterClicked,
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 12.dp, end = 12.dp),
                onClick = onNewNoteClick
            ) {
                Icon(AppIcons.add, "Add new Note")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val isEmpty = result.notes.isEmpty()

                SortBar(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    searchModel = searchModel,
                    onSortUpdate = onSortUpdate,
                    folders = result.folders,
                    selectedFolderId = result.selectedFolderId,
                    onFolderSelected = onFolderSelected,
                    onNewFolderPressed = onNewFolderPressed,
                    onFolderRenameRequest = onFolderRenameRequest,
                    onFolderDeleteRequest = onFolderDeleteRequest,
                    showFolderChips = result.isFolderChipRowVisible,
                )
                if (isEmpty) {
                    val placeholderText = if (searchModel.query.isNullOrEmpty()) {
                        stringResource(R.string.main_screen_placeholder)
                    } else {
                        stringResource(R.string.main_screen_search_placeholder, searchModel.query ?: 0)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        TextBodyMedium(
                            modifier = Modifier.padding(all = 16.dp),
                            textAlign = TextAlign.Center,
                            text = placeholderText
                        )
                    }
                } else {
                    NotesList(
                        modifier = Modifier.fillMaxSize(),
                        items = result.notes,
                        isSelectionMode = result.isSelectionMode,
                        onClick = onItemClick,
                        onLongClick = { id -> onItemSelected(id, true) },
                        onItemSelected = onItemSelected
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun StateResult_Default_Preview() {
    TodoListTheme {
        val note = NotesListItemModel(
            id = 0L,
            title = "Test Note",
            status = NotesListItemState.IN_PROGRESS,
            lastUpdatedTimestamp = "2023-01-01T10:00:00Z",
            numberOfTasks = 3
        )

        val searchModel = SearchModel()
        val notes = List(10) { note.copy(id = it.toLong(), title = "Test Note $it") }
        val screenState by remember { mutableStateOf(MainScreenState(notes = notes)) }

        StateResult(
            modifier = Modifier.fillMaxSize(),
            searchModel = searchModel,
            result = screenState
        )
    }

}

@Preview(showBackground = true)
@Composable
private fun StateResult_EmptyNotes_Preview() {
    TodoListTheme {
        val searchModel = SearchModel()
        val screenState by remember { mutableStateOf(MainScreenState(notes = listOf())) }
        StateResult(
            modifier = Modifier.fillMaxSize(),
            searchModel = searchModel,
            result = screenState
        )
    }
}
