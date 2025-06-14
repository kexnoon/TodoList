package de.telma.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.telma.todolist.core.ui.theme.TodoListTheme
import de.telma.todolist.ui.navigation.ActivityNavEvent
import de.telma.todolist.ui.navigation.ComposableNavEvent
import de.telma.todolist.ui.navigation.Destination
import de.telma.todolist.ui.navigation.NavigationCoordinator
import de.telma.todolist.ui.navigation.dummyScreenOne
import de.telma.todolist.ui.navigation.dummyScreenThree
import de.telma.todolist.ui.navigation.dummyScreenTwo
import de.telma.todolist.ui.navigation.mainScreen
import kotlinx.coroutines.launch
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
                    mainScreen()
                    dummyScreenOne()
                    dummyScreenTwo()
                    dummyScreenThree()
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