package de.telma.todolist.feature_main.main_screen.states

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.TextBodyMedium
import de.telma.todolist.core_ui.theme.TodoListTheme
import de.telma.todolist.feature_main.R
import de.telma.todolist.feature_main.main_screen.MainScreenUiErrors

@Composable
internal fun StateError(
    modifier: Modifier = Modifier,
    error: MainScreenUiErrors,
    onRetryPressed: () -> Unit = {}
) {
    TodoListTheme {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextBodyMedium(text = stringResource(R.string.main_screen_error_title))
            TextBodyMedium(text = errorHandler(error))
            Button(onClick = onRetryPressed) {
                Text(stringResource(R.string.main_screen_error_retry))
            }
        }
    }
}

@Composable
private fun errorHandler(error: MainScreenUiErrors): String {
    return when (error) {
        is MainScreenUiErrors.FailedToCreateNewNote ->
            stringResource(R.string.main_screen_error_failed_to_create_new_note)
        is MainScreenUiErrors.FailedToDeleteNotes ->
            stringResource(R.string.main_screen_error_failed_to_delete_notes)
    }
}

@Preview(showBackground = true)
@Composable
private fun StateError_Preview() {
    TodoListTheme {
        StateError(
            modifier = Modifier.fillMaxSize(),
            error = MainScreenUiErrors.FailedToCreateNewNote
        )
    }
}
