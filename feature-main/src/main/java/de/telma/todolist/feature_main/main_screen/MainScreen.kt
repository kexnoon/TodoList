package de.telma.todolist.feature_main.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.TodoListTheme

@Composable
internal fun MainScreen(
    viewModel: MainScreenViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when(uiState) {
                is UiState.Loading -> StateLoading(modifier = Modifier.fillMaxSize())
                is UiState.Result<*> -> StateResult(
                    modifier = Modifier.fillMaxSize(),
                    result = (uiState as UiState.Result<MainScreenState>).data
                )
                is UiState.Error-> StateError(
                    modifier = Modifier.fillMaxSize(),
                    errorMessage = (uiState as UiState.Error).throwable.message ?: "Something went wrong!"
                )
            }
        }
    }
}

@Composable
private fun StateLoading(modifier: Modifier = Modifier) {

}

@Composable
private fun StateResult(modifier: Modifier = Modifier, result: MainScreenState) {

}

@Composable
private fun StateError(modifier: Modifier = Modifier, errorMessage: String) {

}

@Preview(showBackground = true)
@Composable
private fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading(modifier = Modifier.fillMaxSize())
    }
}

@Preview(showBackground = true)
@Composable
private fun StateResult_Preview() {
    TodoListTheme {
        val screenState = MainScreenState()
        StateResult(modifier = Modifier.fillMaxSize(), screenState)
    }
}
@Preview(showBackground = true)
@Composable
private fun StateError_Preview() {
    TodoListTheme {
        val errorMessage = "Something went wrong!"
        StateError(modifier = Modifier.fillMaxSize(), errorMessage = errorMessage)
    }
}
