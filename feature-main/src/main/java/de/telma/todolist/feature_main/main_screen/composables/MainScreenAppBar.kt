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
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.AppIcons


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenAppBar(
    modifier: Modifier,
    isSelectionMode: Boolean,
    selectionCount: Int = 0,
    onDeleteClick: () -> Unit = {},
    onClearSelectionClick: () -> Unit = {}
) {
    Box(modifier = modifier) {
        TopAppBar(
            title = {
                var title = if (!isSelectionMode) "All Notes" else "Selected: $selectionCount"
                Text(title)
            },
            navigationIcon = {
                if (isSelectionMode) {
                    IconButton(onClick = onClearSelectionClick) {
                        Icon(
                            imageVector = AppIcons.clear,
                            contentDescription = "Clear Selection"
                        )
                    }
                }
            },
            actions = {
                if (isSelectionMode) {
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = AppIcons.delete,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        )
    }
}


@Composable
@Preview(showBackground = true)
fun MainScreenAppBar_Default_Preview() {
    MainScreenAppBar(
        modifier = Modifier,
        isSelectionMode = false
    )
}

@Composable
@Preview(showBackground = true)
fun MainScreenAppBar_SelectionMode_Preview() {
    MainScreenAppBar(
        modifier = Modifier,
        isSelectionMode = true,
        selectionCount = 3
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun MainScreenAppBar_Playground_Preview() {
    var selectionMode by rememberSaveable { mutableStateOf(true) }
    var showAlertDialog by rememberSaveable { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainScreenAppBar(
                modifier = Modifier.fillMaxWidth(),
                isSelectionMode = selectionMode,
                selectionCount = 3,
                onClearSelectionClick = {  selectionMode = false },
                onDeleteClick = { showAlertDialog = true }
            )
            if (!selectionMode) {
                Button(onClick = { selectionMode = true }) {
                    Text("Restore selection mode")
                }
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

