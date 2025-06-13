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
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import de.telma.todolist.ui.main_screen.MainScreen
import de.telma.todolist.ui.main_screen.MainScreenViewModel
import de.telma.todolist.core.ui.theme.TodoListTheme
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOne
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOneViewModel
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwo
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.todolist.ui.dummy_screen_3.DummyScreenThree
import de.telma.todolist.ui.dummy_screen_3.DummyScreenThreeViewModel
import de.telma.todolist.ui.navigation.ActivityNavEvent
import de.telma.todolist.ui.navigation.ComposableNavEvent
import de.telma.todolist.ui.navigation.Destination
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
                    composable<Destination.DummyScreenThree>(
                        deepLinks = listOf(navDeepLink { uriPattern = "myapp://screen_three/{number}" })
                    ) {
                        val args = it.toRoute<Destination.DummyScreenThree>()
                        val viewModel = koinViewModel<DummyScreenThreeViewModel>()
                        DummyScreenThree(
                            viewModel = viewModel,
                            number = args.number
                        )
                    }
                }

                val coordinator: NavigationCoordinator = koinInject()

                this.lifecycleScope.launch() {
                    repeatOnLifecycle (Lifecycle.State.STARTED) {
                        coordinator.navEvents.collect { event ->
                            when(event) {
                                is ComposableNavEvent -> event.execute(navController)
                                is ActivityNavEvent -> event.execute(this@MainActivity)
                            }
                        }
                    }
                }
            }
        }
    }
}