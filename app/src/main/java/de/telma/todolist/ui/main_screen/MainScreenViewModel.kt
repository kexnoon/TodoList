package de.telma.todolist.ui.main_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.UiEvents
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.core.ui.state.toUiState
import de.telma.todolist.data.NoteRepository
import de.telma.todolist.data.model.Note
import de.telma.todolist.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.collections.List

class MainScreenViewModel(private val repository: NoteRepository): BaseViewModel<List<Note>, UiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<List<Note>>>(UiState.Loading())
    override var _uiEvents = MutableStateFlow<UiEvents?>(null)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            repository.getNotes().collectLatest {
                _uiState.value = it.toUiState()
            }
        }
    }
}