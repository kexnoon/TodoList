package de.telma.todolist.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.core.ui.state.toUiState
import de.telma.todolist.data.NoteRepository
import de.telma.todolist.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.collections.List


class MainScreenViewModel(
    private val repository: NoteRepository
): ViewModel() {
    val uiState: StateFlow<UiState<List<Note>>>
        get() = _uiState.asStateFlow()
    private var _uiState = MutableStateFlow<UiState<List<Note>>>(UiState.Loading())

    init {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getNotes().collectLatest {
                _uiState.value = it.toUiState()
            }
        }
    }
}