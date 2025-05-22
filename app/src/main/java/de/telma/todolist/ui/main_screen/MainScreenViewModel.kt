package de.telma.todolist.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.core.ui.state.toUiState
import de.telma.todolist.data.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainScreenViewModel(
    private val repository: NoteRepository
): ViewModel() {
    val uiState: StateFlow<UiState<String>>
        get() = _uiState.asStateFlow()
    private var _uiState = MutableStateFlow<UiState<String>>(UiState.Loading())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            val message = repository.getMessage().collectLatest { message ->
                _uiState.value = message.toUiState()
            }
        }
    }
}