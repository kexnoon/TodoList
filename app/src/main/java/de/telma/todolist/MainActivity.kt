package de.telma.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import de.telma.todolist.ui.main_screen.MainScreen
import de.telma.todolist.ui.main_screen.MainScreenViewModel
import de.telma.todolist.core.ui.theme.TodoListTheme
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOne
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOneViewModel
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwo
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.todolist.ui.navigation.Destination
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Destination.MainScreen
                ) {
                    composable<Destination.MainScreen> {
                        val viewModel = koinViewModel<MainScreenViewModel>()
                        MainScreen(navController, viewModel)
                    }
                    composable<Destination.DummyScreenOne> {
                        val viewModel = koinViewModel<DummyScreenOneViewModel>()
                        DummyScreenOne(navController, viewModel)
                    }
                    composable<Destination.DummyScreenTwo> {
                        val args = it.toRoute<Destination.DummyScreenTwo>()
                        val viewModel = koinViewModel<DummyScreenTwoViewModel>()
                        DummyScreenTwo(
                            navController = navController,
                            viewModel = viewModel,
                            message = args.message
                        )
                    }
                }
            }
        }
    }
}