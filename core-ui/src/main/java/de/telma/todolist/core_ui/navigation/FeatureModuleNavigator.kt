package de.telma.todolist.core_ui.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable

abstract class FeatureModuleNavigator {
    @Serializable
    abstract val startDestination: Destination
}