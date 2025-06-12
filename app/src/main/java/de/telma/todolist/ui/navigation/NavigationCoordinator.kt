package de.telma.todolist.ui.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class NavigationCoordinator {
    private val _navEvents = MutableSharedFlow<NavEvent>()
    val navEvents: SharedFlow<NavEvent>
        get() = _navEvents

    suspend fun execute(navEvent: NavEvent) {
        _navEvents.emit(navEvent)
    }
}