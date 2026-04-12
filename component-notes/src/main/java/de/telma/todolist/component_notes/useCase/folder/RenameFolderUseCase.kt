package de.telma.todolist.component_notes.useCase.folder

class RenameFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
    }

    suspend operator fun invoke(): Result {
        return Result.SUCCESS
    }
}
