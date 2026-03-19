package de.telma.todolist.feature_main.main_screen.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.AppIcons
import de.telma.todolist.feature_main.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenAppBar(
    modifier: Modifier,
    state: MainScreenAppBarState = MainScreenAppBarState.Default,
    onDeleteClick: () -> Unit = {},
    onClearSelectionClick: () -> Unit = {}
) {
    val title = when (state) {
        is MainScreenAppBarState.Selection ->
            stringResource(R.string.main_screen_app_bar_title_selection_mode, state.count)
        is MainScreenAppBarState.Search ->
            stringResource(R.string.main_screen_app_bar_title_search_mode, state.count)
        MainScreenAppBarState.Default ->
            stringResource(R.string.main_screen_app_bar_title_default)
    }

    Box(modifier = modifier) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                if (state is MainScreenAppBarState.Selection) {
                    IconButton(onClick = onClearSelectionClick) {
                        Icon(
                            imageVector = AppIcons.clear,
                            contentDescription = stringResource(R.string.main_screen_app_bar_action_clear_selection)
                        )
                    }
                }
            },
            actions = {
                if (state is MainScreenAppBarState.Selection) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = AppIcons.delete,
                            contentDescription = stringResource(R.string.main_screen_app_bar_action_delete)
                        )
                    }
                }
            }
        )
    }
}


@Composable
@Preview(showBackground = true)
private fun MainScreenAppBar_Default_Preview() {
    MainScreenAppBar(
        modifier = Modifier,
        state = MainScreenAppBarState.Default
    )
}

@Composable
@Preview(showBackground = true)
private fun MainScreenAppBar_SelectionMode_Preview() {
    MainScreenAppBar(
        modifier = Modifier,
        state = MainScreenAppBarState.Selection(count = 3)
    )
}

@Composable
@Preview(showBackground = true)
private fun MainScreenAppBar_Search_Preview() {
    MainScreenAppBar(
        modifier = Modifier,
        state = MainScreenAppBarState.Search(count = 2)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
private fun MainScreenAppBar_Playground_Preview() {
    var selectionMode by rememberSaveable { mutableStateOf(true) }
    var searchMode by rememberSaveable { mutableStateOf(false) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainScreenAppBar(
                modifier = Modifier.fillMaxWidth(),
                state = when {
                    searchMode -> MainScreenAppBarState.Search(count = 2)
                    selectionMode -> MainScreenAppBarState.Selection(count = 3)
                    else -> MainScreenAppBarState.Default
                },
                onClearSelectionClick = { selectionMode = false },
                onDeleteClick = { showAlertDialog = true }
            )
            if (!selectionMode) {
                Button(onClick = { selectionMode = true }) {
                    Text("Restore selection mode")
                }
            }
            Button(onClick = { searchMode = !searchMode }) {
                Text("Toggle search mode")
            }
        }

        if (showAlertDialog) {
            AlertDialog(onDismissRequest = { showAlertDialog = false }) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(color = Color(0xFFFFFFFF)),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(all = 8.dp),
                        text = "Delete button was pressed"
                    )
                    Button(onClick = { showAlertDialog = false }) {
                        Text(text = "OK")
                    }
                }
            }
        }
    }
}

sealed interface MainScreenAppBarState {
    data object Default : MainScreenAppBarState
    data class Selection(val count: Int) : MainScreenAppBarState
    data class Search(val count: Int) : MainScreenAppBarState
}
