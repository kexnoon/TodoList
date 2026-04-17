package de.telma.todolist.feature_main.note_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.GetFoldersUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.SetNoteFolderUseCase
import de.telma.todolist.component_notes.useCase.task.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.task.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.note.RenameNoteUseCase
import de.telma.todolist.component_notes.useCase.task.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.task.UpdateTaskStatusUseCase
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.BaseUiError
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.feature_main.note_screen.models.NoteScreenAppBarModel
import de.telma.todolist.feature_main.note_screen.models.CurrentFolderModel
import de.telma.todolist.feature_main.note_screen.models.TaskItemModel
import de.telma.todolist.feature_main.note_screen.models.toTaskItemModel
import de.telma.todolist.feature_main.utils.toCurrentFolderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NoteScreenViewModel(
    private val noteId: Long,
    private val coordinator: NavigationCoordinator,

    private val noteRepository: NoteRepository,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val createFolderUseCase: CreateFolderUseCase,
    private val setNoteFolderUseCase: SetNoteFolderUseCase,
    private val renameNoteUseCase: RenameNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,

    private val createNewTaskUseCase: CreateNewTaskUseCase,
    private val renameTaskUseCase: RenameTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,

    private val syncNoteStatusUseCase: SyncNoteStatusUseCase

) : BaseViewModel<NoteScreenState, NoteScreenUiEvents?, NoteScreenUiErrors>() {
    override var _uiState: MutableStateFlow<UiState<NoteScreenState, NoteScreenUiErrors>> =
        MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<NoteScreenUiEvents?> = MutableStateFlow(null)

    private lateinit var currentNote: Note
    private lateinit var selectedFolderModel: CurrentFolderModel
    private lateinit var availableFolders: List<Folder>

    init {
        getNoteById()
    }

    fun getNoteById() {
        viewModelScope.launch {
            noteRepository.getNoteById(noteId).collect { note ->
                if (note == null) {
                    showError(NoteScreenUiErrors.NoteNotFound(noteId))
                } else {
                    currentNote = note
                    updateFolders(note.folderId)

                    val appBarState = NoteScreenAppBarModel(
                        noteId = note.id,
                        title = note.title,
                        isComplete = note.status == NoteStatus.COMPLETE,
                        currentFolder = selectedFolderModel
                    )

                    val taskModels = note.tasksList.map { it.toTaskItemModel() }

                    val screenState = NoteScreenState(
                        noteId = note.id,
                        appBar = appBarState,
                        tasks = taskModels,
                        availableFolders = availableFolders,
                        currentFolder = selectedFolderModel
                    )

                    showResult(screenState)
                }
            }
        }
    }

    fun updateTaskStatus(taskId: Long) {
        viewModelScope.launch {
            val currentTask = currentNote.tasksList.find { it.id == taskId }
            if (currentTask == null) {
                showError(NoteScreenUiErrors.TaskNotFound(taskId))
                return@launch
            } else {
                val result = async {
                    updateTaskStatusUseCase(noteId, currentTask, currentTask.getOppositeStatus())
                }.await()

                if (result == UpdateTaskStatusUseCase.Result.FAILURE) {
                    showError(NoteScreenUiErrors.FailedToUpdateTaskStatus(taskId))
                } else {
                    sync(viewModelScope)
                }
            }
        }
    }


    fun deleteNote() {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)
            val result = async { deleteNoteUseCase(currentNote) }.await()
            if (result) {
                coordinator.execute(NavEvent.PopBack)
            } else {
                showError(NoteScreenUiErrors.FailedToDeleteNote(noteId))
            }
        }
    }


    fun renameNote(newTitle: String) {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)

            val result = renameNoteUseCase(currentNote, newTitle)
            if (result == RenameNoteUseCase.Result.SUCCESS) {
                sync(viewModelScope)
            } else {
                showError(NoteScreenUiErrors.FailedToRenameNote(noteId))
            }

        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)

            val addTaskResult = async { createNewTaskUseCase(currentNote, title) }.await()
            if (addTaskResult == CreateNewTaskUseCase.Result.FAILURE) {
                showError(NoteScreenUiErrors.FailedToCreateNewTask)
            } else {
                sync(viewModelScope)
            }
        }
    }

    fun renameTask(taskId: Long, newTitle: String) {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)

            val currentTask = currentNote.tasksList.find { it.id == taskId }
            if (currentTask == null) {
                showError(NoteScreenUiErrors.TaskNotFound(taskId))
                return@launch
            }

            val result = async { renameTaskUseCase(noteId, currentTask, newTitle) }.await()
            if (result == RenameTaskUseCase.Result.FAILURE) {
                showError(NoteScreenUiErrors.FailedToRenameTask(taskId))
            } else {
                sync(viewModelScope)
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val currentTask = currentNote.tasksList.find { it.id == taskId }
            if (currentTask == null) {
                showError(NoteScreenUiErrors.TaskNotFound(taskId))
                return@launch
            }

            val result = async { deleteTaskUseCase(currentNote.id, currentTask) }.await()
            if (result == DeleteTaskUseCase.Result.FAILURE)
                showError(NoteScreenUiErrors.FailedToDeleteTask(taskId))
            else
                sync(viewModelScope)
        }
    }

    fun onDeleteNotePressed() {
        showUiEvent(NoteScreenUiEvents.ShowDeleteNoteDialog)
    }

    fun onRenameNotePressed() {
        showUiEvent(NoteScreenUiEvents.ShowNoteRenameDialog(currentNote.title))
    }

    fun onAddTaskPressed() {
        showUiEvent(NoteScreenUiEvents.ShowAddTaskDialog)
    }

    fun onTaskRenamePressed(taskId: Long) {
        val currentTask = currentNote.tasksList.find { it.id == taskId }
        if (currentTask == null) {
            showError(NoteScreenUiErrors.TaskNotFound(taskId))
            return
        } else {
            showUiEvent(NoteScreenUiEvents.ShowTaskRenameDialog(taskId, currentTask.title))
        }
    }

    fun dismissDialog() {
        showUiEvent(NoteScreenUiEvents.DismissDialog)
    }

    fun retryOnError() {
        showLoading()
        getNoteById()
    }

    fun onBackPressed() {
        viewModelScope.launch {
            coordinator.execute(NavEvent.PopBack)
        }
    }

    fun onCreateFolderPressed() {
        showUiEvent(NoteScreenUiEvents.ShowCreateFolderDialog)
    }

    fun dismissCreateFolderDialog() {
        showUiEvent(NoteScreenUiEvents.DismissCreateFolderDialog)
    }

    fun onFolderSelected(targetFolderId: Long?) {
        if (currentNote.folderId == targetFolderId)
            return

        viewModelScope.launch {
            applyFolderSelection(targetFolderId)
        }
    }

    fun createFolderAndAssign(folderName: String) {
        viewModelScope.launch {
            when (val createResult = createFolderUseCase(folderName)) {
                is CreateFolderUseCase.Result.SUCCESS -> {
                    val isAssigned = applyFolderSelection(createResult.folderId)
                    if (!isAssigned)
                        return@launch

                    updateFolders(createResult.folderId)
                    updateCurrentState()
                    showUiEvent(NoteScreenUiEvents.DismissCreateFolderDialog)
                }

                CreateFolderUseCase.Result.INVALID_NAME -> {
                    showError(NoteScreenUiErrors.FailedToRenameNote(noteId))
                }

                CreateFolderUseCase.Result.FAILURE -> {
                    showError(NoteScreenUiErrors.FailedToRenameNote(noteId))
                }
            }
        }
    }

    private suspend fun applyFolderSelection(targetFolderId: Long?): Boolean {
        val result = setNoteFolderUseCase(noteId, targetFolderId)
        if (result == SetNoteFolderUseCase.Result.FAILURE) {
            showError(NoteScreenUiErrors.FailedToRenameNote(noteId))
            return false
        }

        currentNote = currentNote.copy(folderId = targetFolderId)
        updateFolders(selectedFolderId = targetFolderId)
        updateCurrentState()

        return true
    }

    private fun updateCurrentState() {
        val currentState = (uiState.value as? UiState.Result<NoteScreenState>)?.data ?: return

        showResult(
            currentState.copy(
                appBar = currentState.appBar.copy(currentFolder = selectedFolderModel),
                currentFolder = selectedFolderModel,
                availableFolders = availableFolders
            )
        )
    }

    private suspend fun updateFolders(selectedFolderId: Long?) {
        availableFolders = getFoldersUseCase().first()
        selectedFolderModel = availableFolders.firstOrNull { folder ->
            folder.id == selectedFolderId
        }?.toCurrentFolderModel() ?: CurrentFolderModel(name = "No folder", folderId = null)
    }

    private suspend fun sync(scope: CoroutineScope) {
        val result = scope.async { syncNoteStatusUseCase(currentNote.id) }.await()
        when (result) {
            SyncNoteStatusUseCase.Result.SyncSucceed -> {
                getNoteById()
            }

            SyncNoteStatusUseCase.Result.SyncFailed -> {
                showError(NoteScreenUiErrors.FailedToSyncNoteStatus(noteId))
            }

            else -> {}
        }
    }
}

sealed interface NoteScreenUiEvents : BaseUiEvents {
    data class ShowTaskRenameDialog(val id: Long, val currentTitle: String) : NoteScreenUiEvents
    data object ShowAddTaskDialog : NoteScreenUiEvents
    data object ShowDeleteNoteDialog : NoteScreenUiEvents
    data class ShowNoteRenameDialog(val currentTitle: String) : NoteScreenUiEvents
    data object ShowCreateFolderDialog : NoteScreenUiEvents
    data object DismissCreateFolderDialog : NoteScreenUiEvents
    data object DismissDialog : NoteScreenUiEvents
}

sealed interface NoteScreenUiErrors : BaseUiError {
    data class NoteNotFound(val noteId: Long) : NoteScreenUiErrors
    data class TaskNotFound(val taskId: Long) : NoteScreenUiErrors
    data class FailedToUpdateTaskStatus(val taskId: Long) : NoteScreenUiErrors
    data class FailedToDeleteNote(val noteId: Long) : NoteScreenUiErrors
    data class FailedToRenameNote(val noteId: Long) : NoteScreenUiErrors
    data object FailedToCreateNewTask : NoteScreenUiErrors
    data class FailedToRenameTask(val taskId: Long) : NoteScreenUiErrors
    data class FailedToDeleteTask(val taskId: Long) : NoteScreenUiErrors
    data class FailedToSyncNoteStatus(val noteId: Long) : NoteScreenUiErrors
}

data class NoteScreenState(
    val noteId: Long,
    val appBar: NoteScreenAppBarModel,
    val tasks: List<TaskItemModel>,
    val currentFolder: CurrentFolderModel,
    val availableFolders: List<Folder> = listOf(),
)
