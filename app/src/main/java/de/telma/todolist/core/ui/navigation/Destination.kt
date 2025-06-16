package de.telma.todolist.core.ui.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    data object MainScreen: Destination()
    @Serializable
    data object DummyScreenOne: Destination()
    @Serializable
    data class DummyScreenTwo(val message: String): Destination()
    @Serializable
    data class DummyScreenThree(val number: Int): Destination()
}