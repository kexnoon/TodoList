package de.telma.todolist.component_notes.useCase.note

import de.telma.todolist.component_notes.model.Note

class MoveNotesToFolderUseCase {
    sealed interface Result {
        data object SUCCESS : Result
        data object FAILURE : Result
    }

    suspend operator fun invoke(
        selectedNotes: List<Note>,
        targetFolderId: Long?
    ): Result {
        return Result.FAILURE
    }
}
