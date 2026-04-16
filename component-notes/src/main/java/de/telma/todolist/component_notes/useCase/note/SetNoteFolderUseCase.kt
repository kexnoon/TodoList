package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import kotlinx.coroutines.flow.first
import java.time.Clock
import java.time.LocalDateTime

class SetNoteFolderUseCase(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {
    sealed interface Result {
        data object SUCCESS : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(noteId: Long, targetFolderId: Long?): Result {
        val note = noteRepository.getNoteById(noteId).first() ?: return Result.FAILURE
        val sourceFolderId = note.folderId
        val timestamp = getTimestamp(LocalDateTime.now(clock))

        val folderUpdated = noteRepository.updateNotesFolder(listOf(noteId), targetFolderId)
        if (!folderUpdated) {
            return Result.FAILURE
        }

        val foldersToUpdate = LinkedHashSet<Long>()
        sourceFolderId?.let { foldersToUpdate.add(it) }
        targetFolderId?.let { foldersToUpdate.add(it) }

        val areFoldersUpdated = when (foldersToUpdate.size) {
            0 -> true
            1 -> folderRepository.updateFolderTimestamp(foldersToUpdate.first(), timestamp)
            else -> folderRepository.updateFolderTimestamp(foldersToUpdate.toList(), timestamp)
        }
        if (!areFoldersUpdated) {
            return Result.FAILURE
        }

        return Result.SUCCESS
    }
}
