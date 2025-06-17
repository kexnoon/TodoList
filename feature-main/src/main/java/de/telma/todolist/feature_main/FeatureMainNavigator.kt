package de.telma.todolist.feature_main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.telma.todolist.core_ui.navigation.Destination
import de.telma.todolist.core_ui.navigation.FeatureModuleNavigator
import de.telma.todolist.feature_main.main_screen.MainScreen
import kotlinx.serialization.Serializable

class FeatureMainNavigator(): FeatureModuleNavigator() {
    @Serializable
    override val startDestination: Destination = FeatureMainDestination.MainScreen
}

fun NavGraphBuilder.featureMain() {
    mainScreen()
}

internal fun NavGraphBuilder.mainScreen() {
    composable<FeatureMainDestination.MainScreen> {
        MainScreen()
    }
}

internal sealed class FeatureMainDestination: Destination {
    @Serializable
    data object MainScreen: FeatureMainDestination()
}