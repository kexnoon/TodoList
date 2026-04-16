package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note
import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class RenameNoteUseCase(
    private val repository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {

    sealed interface Result {
        object SUCCESS: Result
        object FAILURE: Result
    }

    suspend operator fun invoke(note: Note, newTitle: String): Result {
        val timestamp = getTimestamp(LocalDateTime.now(clock))
        val updatedNote = note.copy(title = newTitle, lastUpdatedTimestamp = timestamp)

        val isNoteUpdated = repository.updateNote(updatedNote)
        if (!isNoteUpdated)
            return Result.FAILURE

        val folderId = note.folderId
        if (folderId != null) {
            val isFolderTimestampUpdated = folderRepository.updateFolderTimestamp(folderId, timestamp)
            if (!isFolderTimestampUpdated)
                return Result.FAILURE
        }

        return Result.SUCCESS
    }

}
