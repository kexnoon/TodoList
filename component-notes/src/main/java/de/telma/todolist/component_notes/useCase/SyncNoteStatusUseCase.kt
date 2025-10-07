package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository
import kotlinx.coroutines.flow.first

class SyncNoteStatusUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(noteId: Long): SyncStatus {
        repository.getNoteById(noteId).first()?.let {
            val tasks = it.tasksList
            val allTasksComplete = tasks.all { it.status == NoteTaskStatus.COMPLETE }

            return if (allTasksComplete && it.status != NoteStatus.COMPLETE) {
                updateNoteStatus(it, NoteStatus.COMPLETE)
            } else if (!allTasksComplete && it.status != NoteStatus.IN_PROGRESS) {
                updateNoteStatus(it, NoteStatus.IN_PROGRESS)
            } else {
                SyncStatus.UP_TO_DATE
            }
        }
        return SyncStatus.SYNC_FAILED
    }

    private suspend fun updateNoteStatus(note: Note, newStatus: NoteStatus): SyncStatus {
        val updatedNote = note.copy(status = newStatus)
        val updateResult = repository.updateNote(updatedNote)
        return if (updateResult) {
            SyncStatus.SYNC_SUCCEED
        } else {
            SyncStatus.SYNC_FAILED
        }
    }

    enum class SyncStatus{ UP_TO_DATE, SYNC_SUCCEED, SYNC_FAILED }

}