package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

// TODO: write unit tests
class CreateNewNoteUseCase(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data class SUCCESS(val newNoteId: Long): Result
        data object FAILURE: Result
    }

    suspend operator fun invoke(title: String, folderId: Long? = null): Result {
        try {
            val timestamp = getTimestamp(LocalDateTime.now(clock))
            val newNoteId = noteRepository.createNewNote(
                title,
                timestamp,
                folderId
            )
            if (folderId != null) {
                val updateResult = folderRepository.updateFolderTimestamp(folderId, timestamp)
                if (!updateResult) {
                    return Result.FAILURE
                }
            }
            return Result.SUCCESS(newNoteId)
        } catch (e: Exception) {
            return Result.FAILURE
        }
    }
}
