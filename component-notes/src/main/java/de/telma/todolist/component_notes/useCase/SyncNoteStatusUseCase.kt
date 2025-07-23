package de.telma.todolist.component_notes.useCase

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.model.NoteStatus
import de.telma.todolist.component_notes.model.NoteTaskStatus
import de.telma.todolist.component_notes.repository.NoteRepository

class SyncNoteStatusUseCase(private val repository: NoteRepository) {

    suspend operator fun invoke(note: Note): SyncStatus {
        val tasks = note.tasksList
        val allTasksComplete = tasks.all { it.status == NoteTaskStatus.COMPLETE }

        return if (allTasksComplete && note.status != NoteStatus.COMPLETE) {
            updateNoteStatus(note, NoteStatus.COMPLETE)
        } else if (!allTasksComplete && note.status != NoteStatus.IN_PROGRESS) {
            updateNoteStatus(note, NoteStatus.IN_PROGRESS)
        } else {
            SyncStatus.UP_TO_DATE
        }
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