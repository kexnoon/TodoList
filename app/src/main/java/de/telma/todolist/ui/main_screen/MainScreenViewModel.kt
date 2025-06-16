package de.telma.todolist.ui.main_screen

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import de.telma.todolist.core.ui.state.EmptyUiEvents
import de.telma.todolist.core.ui.state.UiState
import de.telma.todolist.data.NoteRepository
import de.telma.todolist.data.model.Note
import de.telma.todolist.ui.base.BaseViewModel
import de.telma.todolist.core.ui.navigation.Destination
import de.telma.todolist.core.ui.navigation.NavEvent
import de.telma.todolist.core.ui.navigation.NavigationCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.collections.List

class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val repository: NoteRepository
): BaseViewModel<List<Note>, EmptyUiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<List<Note>>>(UiState.Result(emptyList()))
    override var _uiEvents = MutableStateFlow<EmptyUiEvents?>(null)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            showLoading()
            repository.getNotes().collectLatest {
                showResult(it)
            }
        }
    }

    fun onButtonOneClick() {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(Destination.DummyScreenOne)
            )
        }
    }

    fun onButtonTwoClick() {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(Destination.DummyScreenTwo("Хуй!"))
            )
        }
    }

    fun onButtonThreeClick() {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen("myapp://screen_three/1337".toUri())
            )
        }
    }

}