package de.telma.todolist.ui.base

import androidx.lifecycle.ViewModel
import de.telma.todolist.core_ui.state.EmptyUiEvents
import de.telma.todolist.core_ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<T, R: EmptyUiEvents?> : ViewModel() {

    val uiState: StateFlow<UiState<T>>
        get() = _uiState.asStateFlow()
    protected abstract var _uiState : MutableStateFlow<UiState<T>>

    val uiEvents: StateFlow<R>
        get() = _uiEvents.asStateFlow()
    protected abstract var _uiEvents : MutableStateFlow<R>

    fun showLoading() {
        _uiState.value = UiState.Loading()
    }

    fun showResult(value: T) {
        _uiState.value = UiState.Result(value)
    }

    fun showError(throwable: Throwable) {
        _uiState.value = UiState.Error(throwable)
    }

    fun showUiEvent(uiEvent: R) {
        _uiEvents.value = uiEvent
    }

}