package de.telma.todolist.feature_main.note_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.composables.TextLabelMedium
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.TodoListTheme

@Composable
fun NoteScreen(
    viewModel: NoteScreenViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val events by viewModel.uiEvents.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when(state) {
            is UiState.Loading -> StateLoading()
            is UiState.Result<NoteScreenState> -> StateResult(state = (state as UiState.Result<NoteScreenState>).data)
            is UiState.Error -> StateError(errorMessage = (state as UiState.Error).throwable.message ?: "Something went wrong!")
        }
    }

    when (events) {
        //todo
    }
}

@Composable
fun StateResult(
    modifier: Modifier = Modifier,
    state: NoteScreenState
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextLabelMedium(text = "NoteId: ${state.noteId}")
    }
}

@Composable
fun StateLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(100.dp))
    }
}

@Composable
fun StateError(
    modifier: Modifier = Modifier,
    errorMessage: String,
    onRetryPressed: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth().wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextLabelMedium(text ="Error! $errorMessage")
        Button(onClick = onRetryPressed) { Text("Retry") }
    }
}

@Composable
@Preview(showBackground = true)
fun StateResult_Preview() {
    TodoListTheme {
        val state = NoteScreenState(noteId = 1)
        StateResult(state = state)
    }
}

@Composable
@Preview(showBackground = true)
fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading()
    }
}

@Composable
@Preview(showBackground = true)
fun StateError_Preview() {
    TodoListTheme {
        StateError(errorMessage = "Something went wrong!")
    }
}