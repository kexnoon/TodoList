package de.telma.todolist.feature_main.main_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.component_notes.model.Filters
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesUseCase
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.BaseUiError
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.feature_main.MainDestination
import de.telma.todolist.feature_main.main_screen.models.NotesListItemModel
import de.telma.todolist.feature_main.main_screen.models.toNotesListItemModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val coordinator: NavigationCoordinator,
    private val getNotesUseCase: GetNotesUseCase,
    private val createNewNoteUseCase: CreateNewNoteUseCase,
    private val deleteNotesUseCase: DeleteNoteUseCase
): BaseViewModel<MainScreenState, MainScreenUiEvents?, MainScreenUiErrors>() {
    override var _uiState: MutableStateFlow<UiState<MainScreenState, MainScreenUiErrors>> = MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<MainScreenUiEvents?> = MutableStateFlow(null)

    private var notes: List<Note> = listOf()
    private var _search: MutableStateFlow<SearchModel> = MutableStateFlow(SearchModel())
    var search: StateFlow<SearchModel> = _search
    private var getNotesJob: Job? = null
    private var mainScreenState = MainScreenState()

    init {
        observeSearch()
        getAllNotes()
    }

    private fun updateScreenState(transform: (MainScreenState) -> MainScreenState) {
        val currentState = (_uiState.value as? UiState.Result<MainScreenState>)?.data ?: mainScreenState
        val newState = transform(currentState)
        mainScreenState = newState
        showResult(newState)
    }

    fun getAllNotes() {
        getNotesJob?.cancel()
        getNotesJob = viewModelScope.launch {
            getNotesUseCase(search = search.value)
                .collect { collectedNotes ->
                    notes = collectedNotes
                    updateScreenState { state ->
                        state.copy(
                            notes = collectedNotes.map { it.toNotesListItemModel() },
                            searchCounter = if (search.value.query.isNullOrEmpty()) 0 else collectedNotes.size
                        )
                    }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            search
                .debounce(timeoutMillis = 300L)
                .distinctUntilChanged()
                .collectLatest {
                    getAllNotes()
                }
        }
    }

    fun onSearchQueryInput(query: String) {
        _search.value = _search.value.copy(query = query)
    }

    fun onClearSearchPressed() {
        _search.value = SearchModel()
    }

    fun onFiltersUpdate(searchModel: SearchModel) {
        _search.value = searchModel
    }

    fun sortNotesList(searchModel: SearchModel) {
        _search.value = searchModel
    }

    fun retryOnError() {
        showLoading()
        getAllNotes()
    }

    fun onNoteSelected(id: Long, isSelected: Boolean) {
        updateScreenState { currentState ->
            val updatedNotes = currentState.notes.map {
                if (it.id == id) {
                    it.copy(isSelected = isSelected)
                } else {
                    it
                }
            }
            val selectedCount = updatedNotes.filter { it.isSelected }.size
            currentState.copy(
                isSelectionMode = true,
                notes = updatedNotes,
                selectedNotesCount = selectedCount
            )
        }
    }

    fun onClearSelectionClicked() {
        updateScreenState { currentState ->
            currentState.copy(
                isSelectionMode = false,
                selectedNotesCount = 0,
                notes = currentState.notes.map { it.copy(isSelected = false) }
            )
        }
    }

    fun deleteSelectedNotes() {
        viewModelScope.launch {
            dismissDeleteDialog()
            val currentState = (_uiState.value as UiState.Result<MainScreenState>).data
            val selectedNotesIds = currentState.notes.filter { it.isSelected }.map { it.id }
            val selectedNotes = notes.filter { selectedNotesIds.contains(it.id) }
            val result = deleteNotesUseCase(selectedNotes)
            if(result == DeleteNoteUseCase.Result.SUCCESS){
                onClearSelectionClicked()
            } else {
                showError(MainScreenUiErrors.FailedToDeleteNotes)
            }
        }
    }

    fun createNewNote(title: String) {
        viewModelScope.launch {
            dismissNewNoteDialog()
            val createNewNoteResult = createNewNoteUseCase(title)
            when (createNewNoteResult) {
                is CreateNewNoteUseCase.Result.SUCCESS -> {
                    coordinator.execute(
                        NavEvent.ToComposeScreen(MainDestination.NoteScreen(createNewNoteResult.newNoteId))
                    )
                }
                is CreateNewNoteUseCase.Result.FAILURE -> {
                    showError(MainScreenUiErrors.FailedToCreateNewNote)
                }
            }
        }
    }

    fun toDetailsScreen(noteId: Long) {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(MainDestination.NoteScreen(noteId))
            )
        }
    }

    fun onNewNoteClicked() {
        showUiEvent(MainScreenUiEvents.ShowCreateNewNoteDialog)
    }

    fun onDeleteClicked() {
        showUiEvent(MainScreenUiEvents.ShowDeleteDialog)
    }

    fun dismissDeleteDialog() {
        showUiEvent(MainScreenUiEvents.DismissDeleteDialog)
    }

    fun dismissNewNoteDialog() {
        showUiEvent(MainScreenUiEvents.DismissCreateNewNoteDialog)
    }
}

sealed interface MainScreenUiEvents: BaseUiEvents {
    data object ShowDeleteDialog: MainScreenUiEvents
    data object ShowCreateNewNoteDialog: MainScreenUiEvents
    data object DismissDeleteDialog: MainScreenUiEvents
    data object DismissCreateNewNoteDialog: MainScreenUiEvents
}

sealed interface MainScreenUiErrors: BaseUiError {
    data object FailedToCreateNewNote: MainScreenUiErrors
    data object FailedToDeleteNotes: MainScreenUiErrors
}

data class MainScreenState(
    val notes: List<NotesListItemModel> = listOf(),
    val selectedNotesCount: Int = 0,
    val searchCounter: Int = 0,
    val isSelectionMode: Boolean = false,
)
