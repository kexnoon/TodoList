package de.telma.todolist.ui.base

import androidx.lifecycle.ViewModel
import de.telma.todolist.core.ui.state.UiEvents
import de.telma.todolist.core.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T, R: UiEvents?> : ViewModel() {

    val uiState: StateFlow<UiState<T>>
        get() = _uiState.asStateFlow()
    protected abstract var _uiState : MutableStateFlow<UiState<T>>

    val uiEvents: StateFlow<R>
        get() = _uiEvents.asStateFlow()
    protected abstract var _uiEvents : MutableStateFlow<R>

}