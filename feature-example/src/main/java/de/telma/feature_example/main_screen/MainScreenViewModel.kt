package de.telma.feature_example.main_screen

import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import de.telma.feature_example.ExampleDestination
import de.telma.todolist.core_ui.state.EmptyUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.collections.List

internal class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val repository: NoteRepository
): BaseViewModel<List<Note>, EmptyUiEvents?>() {
    override var _uiState = MutableStateFlow<UiState<List<Note>>>(UiState.Result(emptyList()))
    override var _uiEvents = MutableStateFlow<EmptyUiEvents?>(null)

    init {
        viewModelScope.launch(Dispatchers.Main) {
            showLoading()
            repository.getAllNotes().collectLatest {
                showResult(it)
            }
        }
    }

    fun onButtonOneClick() {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(ExampleDestination.DummyScreenOne)
            )
        }
    }

    fun onButtonTwoClick() {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(ExampleDestination.DummyScreenTwo("Хуй!"))
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