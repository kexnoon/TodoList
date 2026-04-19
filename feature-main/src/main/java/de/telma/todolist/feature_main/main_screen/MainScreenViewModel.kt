package de.telma.todolist.feature_main.main_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.SearchModel
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.DeleteFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.GetFoldersUseCase
import de.telma.todolist.component_notes.useCase.folder.RenameFolderUseCase
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
    private val getFoldersUseCase: GetFoldersUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val renameFolderUseCase: RenameFolderUseCase,
    private val deleteFolderUseCase: DeleteFolderUseCase,
    private val createNewNoteUseCase: CreateNewNoteUseCase,
    private val deleteNotesUseCase: DeleteNoteUseCase
): BaseViewModel<MainScreenState, MainScreenUiEvents?, MainScreenUiErrors>() {
    override var _uiState: MutableStateFlow<UiState<MainScreenState, MainScreenUiErrors>> = MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<MainScreenUiEvents?> = MutableStateFlow(null)

    private var notes: List<Note> = listOf()
    private var folders: List<Folder> = listOf()
    private var _search: MutableStateFlow<SearchModel> = MutableStateFlow(SearchModel())
    var search: StateFlow<SearchModel> = _search
    private var getNotesJob: Job? = null
    private var getFoldersJob: Job? = null
    private var mainScreenState = MainScreenState()

    init {
        observeFolders()
        observeSearch()
        updateFolderUiState()
        getAllNotes()
    }

    fun getAllNotes() {
        getNotesJob?.cancel()
        val isSearchActive = isSearchActive()
        val selectedFolderId = if (isSearchActive) null else currentSelectedFolderId()
        getNotesJob = viewModelScope.launch {
            getNotesUseCase(search.value, selectedFolderId)
                .collect { collectedNotes ->
                    notes = collectedNotes
                    updateScreenState { state ->
                        state.copy(
                            notes = collectedNotes.map { it.toNotesListItemModel() },
                            searchCounter = if (isSearchActive) collectedNotes.size else null,
                            isFolderChipRowVisible = !isSearchActive
                        )
                    }
            }
        }
    }

    private fun observeFolders() {
        getFoldersJob?.cancel()
        getFoldersJob = viewModelScope.launch {
            getFoldersUseCase().collectLatest { collectedFolders ->
                folders = collectedFolders
                val selectedFolderId = currentSelectedFolderId()
                val normalizedSelectedFolderId = if (
                    selectedFolderId != null && folders.none { it.id == selectedFolderId }
                ) {
                    null
                } else {
                    selectedFolderId
                }

                updateScreenState { state ->
                    state.copy(
                        selectedFolderId = normalizedSelectedFolderId,
                        folders = folders,
                        isFolderChipRowVisible = !isSearchActive()
                    )
                }

                if (normalizedSelectedFolderId != selectedFolderId && !isSearchActive()) {
                    getAllNotes()
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
            val createNewNoteResult = createNewNoteUseCase(title, currentSelectedFolderId())
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

    fun onSearchQueryInput(query: String) {
        _search.value = _search.value.copy(query = query)
    }

    fun onSearchModelUpdate(searchModel: SearchModel) {
        _search.value = searchModel
    }

    fun onClearSearchClicked() {
        _search.value = SearchModel()
    }

    fun showFilterDialog() {
        showUiEvent(MainScreenUiEvents.ShowFilterDialog)
    }

    fun dismissFilterDialog() {
        showUiEvent(MainScreenUiEvents.DismissFilterDialog)
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

    fun showCreateFolderDialog() {
        showUiEvent(MainScreenUiEvents.ShowCreateFolderDialog)
    }

    fun dismissCreateFolderDialog() {
        showUiEvent(MainScreenUiEvents.DismissCreateFolderDialog)
    }

    fun dismissRenameFolderDialog() {
        showUiEvent(MainScreenUiEvents.DismissRenameFolderDialog)
    }

    fun dismissDeleteFolderDialog() {
        showUiEvent(MainScreenUiEvents.DismissDeleteFolderDialog)
    }

    fun onFolderSelected(folderId: Long?) {
        updateScreenState { state ->
            state.copy(
                selectedFolderId = folderId,
                isFolderChipRowVisible = !isSearchActive()
            )
        }
        if (!isSearchActive()) {
            getAllNotes()
        }
    }

    fun onFolderRenameRequested(folderId: Long) {
        val folder = folders.firstOrNull { it.id == folderId } ?: return
        showUiEvent(MainScreenUiEvents.ShowRenameFolderDialog(folder.id, folder.name))
    }

    fun onNewFolderPressed() {
        showCreateFolderDialog()
    }

    fun onFolderDeleteRequested(folderId: Long) {
        val folder = folders.firstOrNull { it.id == folderId } ?: return
        showUiEvent(MainScreenUiEvents.ShowDeleteFolderDialog(folder.id, folder.name))
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            when (val result = createFolderUseCase(name)) {
                is CreateFolderUseCase.Result.SUCCESS -> {
                    dismissCreateFolderDialog()
                    onFolderSelected(result.folderId)
                }

                is CreateFolderUseCase.Result.INVALID_NAME -> Unit
                is CreateFolderUseCase.Result.FAILURE -> Unit
            }
        }
    }

    fun renameFolder(folderId: Long, name: String) {
        viewModelScope.launch {
            when (renameFolderUseCase(folderId, name)) {
                is RenameFolderUseCase.Result.SUCCESS -> dismissRenameFolderDialog()
                is RenameFolderUseCase.Result.INVALID_NAME -> Unit
                is RenameFolderUseCase.Result.FAILURE -> Unit
            }
        }
    }

    fun deleteFolder(folderId: Long) {
        viewModelScope.launch {
            when (deleteFolderUseCase(folderId)) {
                is DeleteFolderUseCase.Result.SUCCESS -> {
                    dismissDeleteFolderDialog()
                    if (currentSelectedFolderId() == folderId) {
                        onFolderSelected(null)
                    }
                }

                is DeleteFolderUseCase.Result.FAILURE -> Unit
            }
        }
    }

    private fun updateFolderUiState() {
        updateScreenState { state ->
            state.copy(
                folders = folders,
                isFolderChipRowVisible = !isSearchActive()
            )
        }
    }

    private fun currentSelectedFolderId(): Long? {
        return getCurrentState().selectedFolderId
    }

    private fun getCurrentState(): MainScreenState {
        return (_uiState.value as? UiState.Result<MainScreenState>)?.data ?: mainScreenState
    }

    private fun isSearchActive(): Boolean {
        return !search.value.query.isNullOrBlank()
    }

    private fun updateScreenState(transform: (MainScreenState) -> MainScreenState) {
        val currentState = (_uiState.value as? UiState.Result<MainScreenState>)?.data ?: mainScreenState
        val newState = transform(currentState)
        mainScreenState = newState
        showResult(newState)
    }

    fun toDetailsScreen(noteId: Long) {
        viewModelScope.launch {
            coordinator.execute(
                NavEvent.ToComposeScreen(MainDestination.NoteScreen(noteId))
            )
        }
    }

}

sealed interface MainScreenUiEvents: BaseUiEvents {
    data object ShowDeleteDialog: MainScreenUiEvents
    data object ShowCreateNewNoteDialog: MainScreenUiEvents
    data object ShowFilterDialog: MainScreenUiEvents
    data object ShowCreateFolderDialog: MainScreenUiEvents
    data object ShowMoveToFolderDialog: MainScreenUiEvents
    data object ShowCreateFolderForMoveDialog: MainScreenUiEvents
    data object ShowMoveFlowError: MainScreenUiEvents
    data class ShowRenameFolderDialog(val folderId: Long, val currentName: String): MainScreenUiEvents
    data class ShowDeleteFolderDialog(val folderId: Long, val currentName: String): MainScreenUiEvents
    data object DismissDeleteDialog: MainScreenUiEvents
    data object DismissCreateNewNoteDialog: MainScreenUiEvents
    data object DismissFilterDialog: MainScreenUiEvents
    data object DismissCreateFolderDialog: MainScreenUiEvents
    data object DismissMoveToFolderDialog: MainScreenUiEvents
    data object DismissCreateFolderForMoveDialog: MainScreenUiEvents
    data object DismissRenameFolderDialog: MainScreenUiEvents
    data object DismissDeleteFolderDialog: MainScreenUiEvents
}

sealed interface MainScreenUiErrors: BaseUiError {
    data object FailedToCreateNewNote: MainScreenUiErrors
    data object FailedToDeleteNotes: MainScreenUiErrors
}

data class MainScreenState(
    val notes: List<NotesListItemModel> = listOf(),
    val folders: List<Folder> = listOf(),
    val selectedFolderId: Long? = null,
    val isFolderChipRowVisible: Boolean = true,
    val selectedNotesCount: Int = 0,
    val searchCounter: Int? = null,
    val isSelectionMode: Boolean = false,
)
