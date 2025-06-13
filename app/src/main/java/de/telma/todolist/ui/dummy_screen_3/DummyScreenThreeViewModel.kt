package de.telma.todolist.ui.dummy_screen_3

import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiEvents
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.ui.base.BaseViewModel
import de.telma.todolist.ui.navigation.Destination
import de.telma.todolist.ui.navigation.NavEvent
import de.telma.todolist.ui.navigation.NavEvent.Toast.*
import de.telma.todolist.ui.navigation.NavigationCoordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DummyScreenThreeViewModel(
    private val coordinator: NavigationCoordinator
): BaseViewModel<Unit, UiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading())
    override var _uiEvents = MutableStateFlow<UiEvents?>(null)

    init {
        viewModelScope.launch {
            delay(1000L)
            _uiState.value = UiState.Result(Unit)
        }
    }

    fun popBackToMainScreen() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.PopTo(destination = Destination.MainScreen, isInclusive = false))
        }
    }

    fun showToast() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.Toast("This is toast", Length.Short))
        }
    }
}