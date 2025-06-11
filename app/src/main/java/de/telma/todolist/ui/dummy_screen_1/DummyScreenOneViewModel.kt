package de.telma.todolist.ui.dummy_screen_1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.core.ui.state.toUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DummyScreenOneViewModel: ViewModel() {
    val uiState: StateFlow<UiState<Boolean>>
        get() = _uiState.asStateFlow()
    private var _uiState = MutableStateFlow<UiState<Boolean>>(UiState.Loading())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            delay(1000L)
            _uiState.value = true.toUiState()
        }
    }
}