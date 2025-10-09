package de.telma.feature_example.dummy_screen_3

import androidx.lifecycle.viewModelScope
import de.telma.feature_example.ExampleDestination
import de.telma.todolist.core_ui.state.EmptyUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavEvent.Toast.*
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class DummyScreenThreeViewModel(
    private val coordinator: NavigationCoordinator
): BaseViewModel<Unit, EmptyUiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<Unit>>(UiState.Result(Unit))
    override var _uiEvents = MutableStateFlow<EmptyUiEvents?>(null)

    init {
        viewModelScope.launch {
            showLoading()
            delay(1000L)
            showResult(Unit)
        }
    }

    fun onPopBackToMainScreen() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.PopTo(destination = ExampleDestination.MainScreen, isInclusive = false))
        }
    }

    fun onShowToastPressed() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.Toast("This is toast", Length.Short))
        }
    }

    fun onShowErrorPressed() {
        viewModelScope.launch {
            showError("Generic Error!")
        }
    }

    fun onBackToMainScreenPressed() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.PopBack)
        }
    }
}