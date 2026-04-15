package de.telma.todolist.component_notes.useCase.folder

class RenameFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
        data object INVALID_NAME : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(folderId: Long, name: String): Result {
        if (name.trim().isEmpty()) {
            return Result.INVALID_NAME
        }

        return Result.FAILURE
    }
}
