package de.telma.todolist.feature_main.main_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.telma.todolist.core_ui.theme.TodoListTheme

@Composable
internal fun MainScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Here will be the main screen of this app")
    }
}

@Preview(showBackground = true)
@Composable
private fun MainScreen_Preview() {
    TodoListTheme {
        MainScreen()
    }
}