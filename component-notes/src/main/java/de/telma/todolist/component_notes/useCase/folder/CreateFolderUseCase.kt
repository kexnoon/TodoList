package de.telma.todolist.component_notes.useCase.folder

class CreateFolderUseCase {
    sealed interface Result {
        data class SUCCESS(val folderId: Long) : Result
        data object INVALID_NAME : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(name: String): Result {
        if (name.trim().isEmpty()) {
            return Result.INVALID_NAME
        }

        return Result.FAILURE
    }
}
