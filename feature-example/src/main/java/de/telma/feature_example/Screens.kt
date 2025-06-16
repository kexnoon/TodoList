package de.telma.feature_example
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import de.telma.feature_example.dummy_screen_1.DummyScreenOne
import de.telma.feature_example.dummy_screen_1.DummyScreenOneViewModel
import de.telma.feature_example.dummy_screen_2.DummyScreenTwo
import de.telma.feature_example.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.feature_example.dummy_screen_3.DummyScreenThree
import de.telma.feature_example.dummy_screen_3.DummyScreenThreeViewModel
import de.telma.feature_example.main_screen.MainScreen
import de.telma.feature_example.main_screen.MainScreenViewModel
import de.telma.todolist.core_ui.navigation.Destination
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.exampleScreens() {
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