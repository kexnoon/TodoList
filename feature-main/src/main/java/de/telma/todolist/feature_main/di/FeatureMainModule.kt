package de.telma.todolist.feature_main.di

import de.telma.todolist.feature_main.main_screen.MainScreenViewModel
import de.telma.todolist.feature_main.note_screen.NoteScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureMainModule = module {
    viewModel {
        MainScreenViewModel(
            coordinator = get(),
            getNotesUseCase = get(),
            getFoldersUseCase = get(),
            createFolderUseCase = get(),
            renameFolderUseCase = get(),
            deleteFolderUseCase = get(),
            createNewNoteUseCase = get(),
            deleteNotesUseCase = get(),
            moveNotesToFolderUseCase = get()
        )
    }

    viewModel {
        NoteScreenViewModel(
            noteId = it.get<Long>(),
            coordinator = get(),
            noteRepository = get(),
            getFoldersUseCase = get(),
            createFolderUseCase = get(),
            setNoteFolderUseCase = get(),
            deleteTaskUseCase = get(),
            renameNoteUseCase = get(),
            renameTaskUseCase = get(),
            updateTaskStatusUseCase = get(),
            syncNoteStatusUseCase = get(),
            createNewTaskUseCase = get(),
            deleteNoteUseCase = get()
        )
    }
}
