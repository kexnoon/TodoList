package de.telma.todolist.component_notes.useCase.folder

class DeleteFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
    }

    suspend operator fun invoke(): Result {
        return Result.SUCCESS
    }
}
