package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDateTime

class SyncNoteStatusUseCase(
    private val repository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {

    sealed interface Result {
        object UpToDate : Result
        object SyncSucceed : Result
        object SyncFailed : Result
    }

    suspend operator fun invoke(noteId: Long): Result {
        repository.getNoteById(noteId).first()?.let {
            val tasks = it.tasksList
            val allTasksComplete = tasks.all { it.status == NoteTaskStatus.COMPLETE }

            return if (allTasksComplete && it.status != NoteStatus.COMPLETE) {
                updateNoteStatus(it, NoteStatus.COMPLETE)
            } else if (!allTasksComplete && it.status != NoteStatus.IN_PROGRESS) {
                updateNoteStatus(it, NoteStatus.IN_PROGRESS)
            } else {
                Result.UpToDate
            }
        }
        return Result.SyncFailed
    }

    private suspend fun updateNoteStatus(note: Note, newStatus: NoteStatus): Result {
        val updatedNote = note.copy(status = newStatus)
        val isNoteUpdated = repository.updateNote(updatedNote)
        if (!isNoteUpdated)
            return Result.SyncFailed

        val folderId = note.folderId
        if (folderId != null) {
            val timestamp = getTimestamp(LocalDateTime.now(clock))
            val isFolderTimestampUpdated = folderRepository.updateFolderTimestamp(folderId, timestamp)
            if (!isFolderTimestampUpdated)
                return Result.SyncFailed
        }

        return Result.SyncSucceed
    }
}
