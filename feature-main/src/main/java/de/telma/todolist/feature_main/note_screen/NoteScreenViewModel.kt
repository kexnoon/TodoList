package de.telma.todolist.feature_main.note_screen

import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow

class NoteScreenViewModel(
    private val noteId: Long
): BaseViewModel<NoteScreenState, NoteScreenUiEvents?>() {
    override var _uiState: MutableStateFlow<UiState<NoteScreenState>> = MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<NoteScreenUiEvents?> = MutableStateFlow(null)

    init {
        showResult(NoteScreenState(noteId))
    }

}

class NoteScreenUiEvents: BaseUiEvents {

}

data class NoteScreenState(
    val noteId: Long
)