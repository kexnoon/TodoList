package de.telma.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import de.telma.todolist.ui.navigation.NavEvent
import de.telma.todolist.ui.navigation.NavigationCoordinator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

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
                        MainScreen(viewModel)
                    }
                    composable<Destination.DummyScreenOne> {
                        val viewModel = koinViewModel<DummyScreenOneViewModel>()
                        DummyScreenOne(viewModel)
                    }
                    composable<Destination.DummyScreenTwo> {
                        val args = it.toRoute<Destination.DummyScreenTwo>()
                        val viewModel = koinViewModel<DummyScreenTwoViewModel>()
                        DummyScreenTwo(
                            viewModel = viewModel,
                            message = args.message
                        )
                    }
                }

                val coordinator: NavigationCoordinator = koinInject()
                this.lifecycleScope.launch() {
                    repeatOnLifecycle (Lifecycle.State.STARTED) {
                        coordinator.navEvents.collect { event ->
                            when(event) {
                                is NavEvent.ToComposeScreen -> {
                                    navController.navigate(event.destination)
                                }
                                is NavEvent.PopBack -> {
                                    navController.popBackStack()
                                }
                                is NavEvent.PopTo -> {
                                    navController.popBackStack(event.destination, event.isInclusive)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}