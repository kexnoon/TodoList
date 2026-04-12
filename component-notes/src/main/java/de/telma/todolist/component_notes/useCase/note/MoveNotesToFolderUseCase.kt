package de.telma.todolist.component_notes.useCase.note

class MoveNotesToFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
    }

    suspend operator fun invoke(): Result {
        return Result.SUCCESS
    }
}
