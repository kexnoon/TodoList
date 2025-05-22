package de.telma.todolist.ui.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.ui.theme.TodoListTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import de.telma.todolist.core.ui.state.UiState


@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is UiState.Loading -> StateLoading()
                is UiState.Result<*> -> StateResult(
                    message = (uiState as UiState.Result<String>).data
                )
            }
        }
    }
}

@Composable
private fun StateLoading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .size(100.dp)
    )
}


@Composable
private fun StateResult(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview_StateResult() {
    TodoListTheme {
        StateLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_StateLoading() {
    TodoListTheme {
        StateResult("Android")
    }
}
