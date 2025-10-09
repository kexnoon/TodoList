package de.telma.todolist.feature_main.note_screen

import androidx.lifecycle.viewModelScope
import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.useCase.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.RenameNoteUseCase
import de.telma.todolist.component_notes.useCase.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateTaskStatusUseCase
import de.telma.todolist.core_ui.base.BaseViewModel
import de.telma.todolist.core_ui.navigation.NavEvent
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import de.telma.todolist.core_ui.state.BaseUiEvents
import de.telma.todolist.core_ui.state.UiState
import de.telma.todolist.feature_main.note_screen.models.NoteScreenAppBarModel
import de.telma.todolist.feature_main.note_screen.models.TaskItemModel
import de.telma.todolist.feature_main.note_screen.models.toTaskItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class NoteScreenViewModel(
    private val noteId: Long,
    private val coordinator: NavigationCoordinator,

    private val noteRepository: NoteRepository,

    private val renameNoteUseCase: RenameNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,

    private val createNewTaskUseCase: CreateNewTaskUseCase,
    private val renameTaskUseCase: RenameTaskUseCase,
    private val updateTaskStatusUseCase: UpdateTaskStatusUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,

    private val syncNoteStatusUseCase: SyncNoteStatusUseCase

) : BaseViewModel<NoteScreenState, NoteScreenUiEvents?>() {
    override var _uiState: MutableStateFlow<UiState<NoteScreenState>> =
        MutableStateFlow(UiState.Loading())
    override var _uiEvents: MutableStateFlow<NoteScreenUiEvents?> = MutableStateFlow(null)

    private lateinit var currentNote: Note

    init {
        getNoteById()
    }

    fun getNoteById() {
        viewModelScope.launch {
            noteRepository.getNoteById(noteId).collect { note ->
                if (note == null) {
                    showError("Note not found! (id = $noteId)")
                } else {
                    currentNote = note

                    val appBarState = NoteScreenAppBarModel(
                        noteId = note.id,
                        title = note.title,
                        isComplete = note.status == NoteStatus.COMPLETE
                    )

                    val taskModels = note.tasksList.map { it.toTaskItemModel() }

                    val screenState = NoteScreenState(
                        noteId = note.id,
                        appBar = appBarState,
                        tasks = taskModels
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
                showError("Task not found! (id = $taskId)")
                return@launch
            } else {
                val result = async {
                    updateTaskStatusUseCase(noteId, currentTask, currentTask.getOppositeStatus())
                }.await()

                if (!result) {
                    showError("Failed to update task status! (id = $taskId)")
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
                showError("Failed to delete note! (id = $noteId)")
            }
        }
    }


    fun renameNote(newTitle: String) {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)

            val result = renameNoteUseCase(currentNote, newTitle)
            if (!result) {
                showError("Failed to rename note! (id = $noteId)")
            } else {
                sync(viewModelScope)
            }

        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            showUiEvent(NoteScreenUiEvents.DismissDialog)

            val addTaskResult = async { createNewTaskUseCase(currentNote, title) }.await()
            if (!addTaskResult) {
                showError("Failed to create new task!")
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
                showError("Task not found! (id = $taskId)")
                return@launch
            }

            val result = async { renameTaskUseCase(noteId, currentTask, newTitle) }.await()
            if (!result) {
                showError("Failed to rename task! (id = $taskId)")
            } else {
                sync(viewModelScope)
            }
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            val currentTask = currentNote.tasksList.find { it.id == taskId }
            if (currentTask == null) {
                showError("Task not found! (id = $taskId)")
                return@launch
            }

            val result = async { deleteTaskUseCase(currentTask) }.await()
            if (!result)
                showError("Failed to delete task! (id = $taskId)")
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
            showError("Task not found! (id = $taskId)")
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

    private suspend fun sync(scope: CoroutineScope) {
        val result = scope.async { syncNoteStatusUseCase(currentNote.id) }.await()
        when (result) {
            SyncNoteStatusUseCase.SyncStatus.SYNC_SUCCEED -> {
                getNoteById()
            }

            SyncNoteStatusUseCase.SyncStatus.SYNC_FAILED -> {
                showError("Failed to sync note status! (id = $noteId)")
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
    data object DismissDialog : NoteScreenUiEvents
}

data class NoteScreenState(
    val noteId: Long,
    val appBar: NoteScreenAppBarModel,
    val tasks: List<TaskItemModel>
)