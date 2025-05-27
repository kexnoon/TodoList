package de.telma.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.telma.todolist.ui.main_screen.MainScreen
import de.telma.todolist.ui.main_screen.MainScreenViewModel
import de.telma.todolist.core.ui.theme.TodoListTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListTheme {
                val viewModel = koinViewModel<MainScreenViewModel>()
                MainScreen(viewModel)
            }
        }
    }
}