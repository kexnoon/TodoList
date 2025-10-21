package de.telma.todolist.core_ui.base

import androidx.lifecycle.ViewModel
import de.telma.todolist.core_ui.state.BaseUiError
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T, R: BaseUiEvents?, E: BaseUiError?> : ViewModel() {

    val uiState: StateFlow<UiState<T, E>>
        get() = _uiState.asStateFlow()
    protected abstract var _uiState : MutableStateFlow<UiState<T, E>>

    val uiEvents: StateFlow<R>
        get() = _uiEvents.asStateFlow()
    protected abstract var _uiEvents : MutableStateFlow<R>

    fun showLoading() {
        _uiState.value = UiState.Loading()
    }

    fun showResult(value: T) {
        _uiState.value = UiState.Result(value)
    }

    fun showError(error: E?) {
        error?.let {
            _uiState.value = UiState.Error(it)
        }
    }

    fun showUiEvent(uiEvent: R?) {
        uiEvent?.let {
            _uiEvents.value = it
        }
    }
}