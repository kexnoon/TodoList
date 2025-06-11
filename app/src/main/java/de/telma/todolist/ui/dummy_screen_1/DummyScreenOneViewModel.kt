package de.telma.todolist.ui.dummy_screen_1

import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiEvents
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.core.ui.state.toUiState
import de.telma.todolist.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DummyScreenOneViewModel: BaseViewModel<Unit, UiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<Unit>>(UiState.Loading())
    override var _uiEvents = MutableStateFlow<UiEvents?>(null)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            delay(1000L)
            _uiState.value = Unit.toUiState()
        }
    }
}