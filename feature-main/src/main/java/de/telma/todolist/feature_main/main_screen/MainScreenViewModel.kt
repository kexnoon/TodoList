package de.telma.todolist.feature_main.main_screen

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.DeleteMultipleNotesUseCase
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val repository: NoteRepository,
    private val createNewNoteUseCase: CreateNewNoteUseCase,
    private val deleteNotesUseCase: DeleteMultipleNotesUseCase
): BaseViewModel<MainScreenState, MainScreenUiEvents?>() {
    override var _uiState: MutableStateFlow<UiState<MainScreenState>> = MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<MainScreenUiEvents?> = MutableStateFlow(null)
}

sealed interface MainScreenUiEvents: BaseUiEvents {
    data object ShowDeleteDialog: MainScreenUiEvents
    data object ShowCreateNewNoteDialog: MainScreenUiEvents
}

data class MainScreenState(
    val notes: List<NotesListItemModel> = listOf(),
    val isSelectionMode: Boolean = false
)