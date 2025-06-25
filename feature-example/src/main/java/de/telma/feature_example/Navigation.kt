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
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

fun NavGraphBuilder.featureExample() {
    mainScreen()
    dummyScreenOne()
    dummyScreenTwo()
    dummyScreenThree()
}

internal fun NavGraphBuilder.mainScreen() {
    composable<ExampleDestination.MainScreen> {
        val viewModel = koinViewModel<MainScreenViewModel>()
        MainScreen(viewModel)
    }
}

internal fun NavGraphBuilder.dummyScreenOne() {
    composable<ExampleDestination.DummyScreenOne> {
        val viewModel = koinViewModel<DummyScreenOneViewModel>()
        DummyScreenOne(viewModel)
    }
}

internal fun NavGraphBuilder.dummyScreenTwo() {
    composable<ExampleDestination.DummyScreenTwo> {
        val args = it.toRoute<ExampleDestination.DummyScreenTwo>()
        val viewModel = koinViewModel<DummyScreenTwoViewModel>()
        DummyScreenTwo(
            viewModel = viewModel,
            message = args.message
        )
    }
}

internal fun NavGraphBuilder.dummyScreenThree() {
    composable<ExampleDestination.DummyScreenThree>(
        deepLinks = listOf(navDeepLink { uriPattern = "myapp://screen_three/{number}" })
    ) {
        val args = it.toRoute<ExampleDestination.DummyScreenThree>()
        val viewModel = koinViewModel<DummyScreenThreeViewModel>()
        DummyScreenThree(
            viewModel = viewModel,
            number = args.number
        )
    }
}

 sealed class ExampleDestination: Destination {
    @Serializable
    data object MainScreen: ExampleDestination()
    @Serializable
    data object DummyScreenOne: ExampleDestination()
    @Serializable
    data class DummyScreenTwo(val message: String): ExampleDestination()
    @Serializable
    data class DummyScreenThree(val number: Int): ExampleDestination()

}