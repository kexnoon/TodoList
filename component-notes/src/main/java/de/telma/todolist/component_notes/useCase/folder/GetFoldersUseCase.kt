package de.telma.todolist.component_notes.useCase.folder

import de.telma.todolist.component_notes.model.Folder
import de.telma.todolist.component_notes.repository.FolderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetFoldersUseCase(
    private val folderRepository: FolderRepository
) {

    operator fun invoke(): Flow<List<Folder>> {
        return flowOf(emptyList())
    }
}
