package de.telma.todolist.feature_main.main_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.DeleteMultipleNotesUseCase
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.models.toNotesListItemModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val repository: NoteRepository,
    private val createNewNoteUseCase: CreateNewNoteUseCase,
    private val deleteNotesUseCase: DeleteMultipleNotesUseCase
): BaseViewModel<MainScreenState, MainScreenUiEvents?>() {
    override var _uiState: MutableStateFlow<UiState<MainScreenState>> = MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<MainScreenUiEvents?> = MutableStateFlow(null)

    //ЕБУЧИЙ КОСТЫЛЬ
    private var notes: List<Note> = listOf()

    init {
        getAllNotes()
    }
    fun getAllNotes() {
        viewModelScope.launch {
            repository.getAllNotes().collect { collectedNotes ->
                notes = collectedNotes
                showResult(
                    MainScreenState(notes = collectedNotes.map { it.toNotesListItemModel() })
                )
            }
        }
    }

    fun retryOnError() {
        showLoading()
        getAllNotes()
    }

    fun onNoteSelected(id: Long, isSelected: Boolean) {
        val currentState = (_uiState.value as UiState.Result<MainScreenState>).data
        val updatedNotes = currentState.notes.map {
            if (it.id == id) {
                it.copy(isSelected = isSelected)
            } else {
                it
            }
        }
        val selectedCount = updatedNotes.filter { it.isSelected }.size
        val newState = currentState.copy(
            isSelectionMode = true,
            notes = updatedNotes,
            selectedNotesCount = selectedCount
        )

        showResult(newState)
    }

    fun onClearSelectionClicked() {
        val currentState = (_uiState.value as UiState.Result<MainScreenState>).data
        val newState = currentState.copy(
            isSelectionMode = false,
            selectedNotesCount = 0,
            notes = currentState.notes.map { it.copy(isSelected = false) }
        )

        showResult(newState)
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            dismissDeleteDialog()
            val currentState = (_uiState.value as UiState.Result<MainScreenState>).data
            val selectedNotesIds = currentState.notes.filter { it.isSelected }.map { it.id }
            val selectedNotes = notes.filter { selectedNotesIds.contains(it.id) }
            deleteNotesUseCase(selectedNotes)
            onClearSelectionClicked()
        }
    }

    fun createNewNote(title: String) {
        viewModelScope.launch {
            dismissNewNoteDialog()
            val newNoteId = createNewNoteUseCase(title)
            //todo: переход на Note Screen
        }
    }

    fun toDetailsScreen(noteId: Long) {
        //todo: переход на Note Screen
    }

    fun onNewNoteClicked() {
        _uiEvents.value = MainScreenUiEvents.ShowCreateNewNoteDialog
    }

    fun onDeleteClicked() {
        _uiEvents.value = MainScreenUiEvents.ShowDeleteDialog
    }

    fun dismissDeleteDialog() {
        _uiEvents.value = MainScreenUiEvents.DismissDeleteDialog
    }

    fun dismissNewNoteDialog() {
        _uiEvents.value = MainScreenUiEvents.DismissCreateNewNoteDialog
    }

    private fun updateResultState(screenState: MainScreenState) {
        val uiState = (_uiState.value as UiState.Result<MainScreenState>)
        val newUiState = uiState.copy(data = screenState)
        _uiState.value = newUiState
    }
}

sealed interface MainScreenUiEvents: BaseUiEvents {
    data object ShowDeleteDialog: MainScreenUiEvents
    data object ShowCreateNewNoteDialog: MainScreenUiEvents
    data object DismissDeleteDialog: MainScreenUiEvents
    data object DismissCreateNewNoteDialog: MainScreenUiEvents
}

data class MainScreenState(
    val notes: List<NotesListItemModel> = listOf(),
    val selectedNotesCount: Int = 0,
    val isSelectionMode: Boolean = false
)