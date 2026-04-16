package de.telma.todolist.component_notes.useCase.task

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class CreateNewTaskUseCase(
    private val taskRepository: TaskRepository,
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data class SUCCESS(val newNoteId: Long): Result
        data object FAILURE: Result
    }

    suspend operator fun invoke(note: Note, title: String): Result {
        val timestamp = getTimestamp(LocalDateTime.now(clock))
        if (taskRepository.createNewTask(note.id, title)) {
            val isNoteUpdated = noteRepository.updateNote(note.copy(lastUpdatedTimestamp = timestamp))
            if (!isNoteUpdated)
                return Result.FAILURE

            val folderId = note.folderId
            if (folderId != null) {
                val isFolderTimestampUpdated = folderRepository.updateFolderTimestamp(folderId, timestamp)
                if (!isFolderTimestampUpdated)
                    return Result.FAILURE
            }

            return Result.SUCCESS(note.id)
        }

        return Result.FAILURE
    }


}
