package de.telma.feature_example.dummy_screen_3

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
internal fun DummyScreenThree(
    number: Int,
    viewModel: DummyScreenThreeViewModel
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
                    number = number,
                    onToastButtonClick = { viewModel.onShowToastPressed() },
                    onErrorClick = { viewModel.onShowErrorPressed() },
                    onPopBackButtonClick = { viewModel.onPopBackToMainScreen() }
                )
                is UiState.Error<*> -> {
                    val throwable = (uiState as UiState.Error).throwable
                    StateError(
                        throwable = throwable,
                        onBackPressed = { viewModel.onBackToMainScreenPressed() }
                    )
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
private fun StateError(
    throwable: Throwable,
    onBackPressed: () -> Unit = {}
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Error! ${throwable.message}")
        Button(modifier = Modifier.wrapContentSize(), onClick = onBackPressed) {
            Text("Back")
        }
    }

}

@Composable
private fun StateResult(
    number: Int = 0,
    onToastButtonClick: () -> Unit = {},
    onErrorClick: () -> Unit = {},
    onPopBackButtonClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Dummy Screen Three. Number: $number"
        )
        Button(modifier = Modifier.wrapContentSize(), onClick = onToastButtonClick) {
            Text("Show Toast")
        }
        Button(modifier = Modifier.wrapContentSize(), onClick = onErrorClick) {
            Text("Show error")
        }
        Button(modifier = Modifier.wrapContentSize(), onClick = onPopBackButtonClick) {
            Text("Pop back to Main Screen")
        }


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