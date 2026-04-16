package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.NoteTask
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import java.time.Clock
import de.telma.todolist.component_notes.utils.getTimestamp
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime

class DeleteTaskUseCase(
    private val repository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        object SUCCESS : Result
        object FAILURE : Result
    }

    suspend operator fun invoke(noteId: Long, task: NoteTask): Result {
        val note = noteRepository.getNoteById(noteId).first() ?: return Result.FAILURE

        val isDeleted = repository.deleteTask(task)
        if (!isDeleted)
            return Result.FAILURE

        val folderId = note.folderId
        if (folderId != null) {
            val timestamp = getTimestamp(LocalDateTime.now(clock))
            val isFolderTimestampUpdated = folderRepository.updateFolderTimestamp(folderId, timestamp)
            if (!isFolderTimestampUpdated)
                return Result.FAILURE
        }

        return Result.SUCCESS
    }
}
