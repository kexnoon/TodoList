package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import kotlinx.coroutines.flow.first

class SyncNoteStatusUseCase(private val repository: NoteRepository) {

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
        val updateResult = repository.updateNote(updatedNote)
        return if (updateResult) {
            Result.SyncSucceed
        } else {
            Result.SyncFailed
        }
    }
}