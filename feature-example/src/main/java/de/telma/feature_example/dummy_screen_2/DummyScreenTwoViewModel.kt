package de.telma.feature_example.dummy_screen_2

import androidx.lifecycle.viewModelScope
import de.telma.todolist.core_ui.state.EmptyUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class DummyScreenTwoViewModel(private val coordinator: NavigationCoordinator): BaseViewModel<Unit, EmptyUiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<Unit>>(UiState.Result(Unit))
    override var _uiEvents = MutableStateFlow<EmptyUiEvents?>(null)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            showLoading()
            delay(1000L)
            showResult(Unit)
        }
    }

    fun onButtonClick() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.PopBack)
        }
    }

}