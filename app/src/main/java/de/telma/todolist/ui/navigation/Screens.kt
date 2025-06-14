package de.telma.todolist.ui.navigation
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOne
import de.telma.todolist.ui.dummy_screen_1.DummyScreenOneViewModel
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwo
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.todolist.ui.dummy_screen_3.DummyScreenThree
import de.telma.todolist.ui.dummy_screen_3.DummyScreenThreeViewModel
import de.telma.todolist.ui.main_screen.MainScreen
import de.telma.todolist.ui.main_screen.MainScreenViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.mainScreen() {
    composable<Destination.MainScreen> {
        val viewModel = koinViewModel<MainScreenViewModel>()
        MainScreen(viewModel)
    }
}

internal fun NavGraphBuilder.dummyScreenOne() {
    composable<Destination.DummyScreenOne> {
        val viewModel = koinViewModel<DummyScreenOneViewModel>()
        DummyScreenOne(viewModel)
    }
}

internal fun NavGraphBuilder.dummyScreenTwo() {
    composable<Destination.DummyScreenTwo> {
        val args = it.toRoute<Destination.DummyScreenTwo>()
        val viewModel = koinViewModel<DummyScreenTwoViewModel>()
        DummyScreenTwo(
            viewModel = viewModel,
            message = args.message
        )
    }
}

internal fun NavGraphBuilder.dummyScreenThree() {
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