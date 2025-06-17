package de.telma.todolist.feature_main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import de.telma.todolist.core_ui.navigation.Destination
import de.telma.todolist.feature_main.main_screen.MainScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.featureMain() {
    mainScreen()
}

internal fun NavGraphBuilder.mainScreen() {
    composable<MainDestination.MainScreen> {
        MainScreen()
    }
}

sealed class MainDestination: Destination {
    @Serializable
    data object MainScreen: MainDestination()
}