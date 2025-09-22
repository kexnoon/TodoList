package de.telma.feature_example.dummy_screen_1

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.theme.TodoListTheme

@Composable
internal fun DummyScreenOne(
    viewModel: DummyScreenOneViewModel
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
                    onButtonClick = { viewModel.onButtonClick() }
                )
                is UiState.Error -> {
                    val throwable = (uiState as UiState.Error).throwable
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
    onButtonClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Dummy Screen One")
        Button(modifier = Modifier.wrapContentSize(), onClick = onButtonClick) {
            Text("Back to Main Screen")
        }
    }
}

@Composable
private fun StateError(throwable: Throwable) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Error! ${throwable.message}")
    }

}

@Preview(showBackground = true)
@Composable
private fun StateLoading_Preview() {
    TodoListTheme {
        StateLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun StateResult_Preview() {
    TodoListTheme {
        StateResult()
    }
}

@Preview(showBackground = true)
@Composable
private fun StateError_Preview() {
    TodoListTheme {
        StateError(Throwable("Throwable"))
    }
}
