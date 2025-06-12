package de.telma.todolist.ui.navigation

sealed class NavEvent {
    data class ToComposeScreen(val destination: Destination): NavEvent()
    data class PopTo(val destination: Destination, val isInclusive: Boolean): NavEvent()
    data object PopBack: NavEvent()
}