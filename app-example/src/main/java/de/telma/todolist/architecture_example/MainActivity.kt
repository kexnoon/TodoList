package de.telma.todolist.architecture_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import de.telma.feature_example.ExampleDestination
import de.telma.feature_example.featureExample
import de.telma.todolist.core_ui.navigation.ActivityNavEvent
import de.telma.todolist.core_ui.navigation.ComposableNavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.theme.TodoListTheme
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
                    startDestination = ExampleDestination.MainScreen
                ) {
                    featureExample()
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
