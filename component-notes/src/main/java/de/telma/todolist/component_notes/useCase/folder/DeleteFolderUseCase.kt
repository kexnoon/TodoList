package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.repository.FolderRepository

class DeleteFolderUseCase(
    private val folderRepository: FolderRepository
) {

    sealed interface Result {
        data object SUCCESS : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(folderId: Long): Result {
        return try {
            if (folderRepository.deleteFolder(folderId)) {
                Result.SUCCESS
            } else {
                Result.FAILURE
            }
        } catch (e: Exception) {
            Result.FAILURE
        }
    }
}
