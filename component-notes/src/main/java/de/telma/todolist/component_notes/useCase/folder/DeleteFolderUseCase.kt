package de.telma.todolist.component_notes.useCase.folder

class DeleteFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(folderId: Long): Result {
        return Result.FAILURE
    }
}
