package de.telma.feature_example.main_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.theme.TodoListTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.component_notes.model.Note

@Composable
internal fun MainScreen(
    viewModel: MainScreenViewModel
) {
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
                    count = (uiState as UiState.Result<List<Note>>).data.size,
                    onButtonOneClick = { viewModel.onButtonOneClick() },
                    onButtonTwoClick = { viewModel.onButtonTwoClick() },
                    onButtonThreeClick = { viewModel.onButtonThreeClick() }
                )
                is UiState.Error -> {
                    val throwable = (uiState as UiState.Error).errorMessage
                    StateError(throwable)
                }
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
private fun StateResult(
    count: Int,
    modifier: Modifier = Modifier,
    onButtonOneClick: () -> Unit = {},
    onButtonTwoClick: () -> Unit = {},
    onButtonThreeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Notes count: $count",
            modifier = modifier
        )

        Button(modifier = Modifier.wrapContentSize(), onClick = onButtonOneClick) {
            Text("To Screen One")
        }

        Button(modifier = Modifier.wrapContentSize(), onClick = onButtonTwoClick) {
            Text("To Screen Two")
        }

        Button(modifier = Modifier.wrapContentSize(), onClick = onButtonThreeClick) {
            Text("To Screen Three")
        }
    }
}

@Composable
private fun StateError(message: String) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Error! $message")
    }

}

@Preview(showBackground = true)
@Composable
private fun Preview_StateLoading() {
    TodoListTheme {
        StateLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview_StateResult() {
    TodoListTheme {
        StateResult(228)
    }
}

@Preview(showBackground = true)
@Composable
private fun StateError_Preview() {
    TodoListTheme {
        StateError("Error message!")
    }
}
