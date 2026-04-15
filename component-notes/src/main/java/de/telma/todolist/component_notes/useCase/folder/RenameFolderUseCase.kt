package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.utils.getTimestamp
import java.time.Clock
import java.time.LocalDateTime

class RenameFolderUseCase(
    private val folderRepository: FolderRepository,
    private val clock: Clock
) {

    sealed interface Result {
        data object SUCCESS : Result
        data object INVALID_NAME : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(folderId: Long, name: String): Result {
        val normalizedName = name.trim()
        if (normalizedName.isEmpty()) {
            return Result.INVALID_NAME
        }

        return try {
            val timestamp = getTimestamp(LocalDateTime.now(clock))
            if (folderRepository.renameFolder(folderId, normalizedName, timestamp)) {
                Result.SUCCESS
            } else {
                Result.FAILURE
            }
        } catch (e: Exception) {
            Result.FAILURE
        }
    }
}
