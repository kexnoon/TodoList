package de.telma.todolist.feature_main.di

import de.telma.todolist.feature_main.main_screen.MainScreenViewModel
import de.telma.todolist.feature_main.note_screen.NoteScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureMainModule = module {
    viewModel {
        MainScreenViewModel(
            coordinator = get(),
            repository = get(),
            createNewNoteUseCase = get(),
            deleteNotesUseCase = get()
        )
    }

    viewModel {
        NoteScreenViewModel(
            noteId = it.get<Long>(),
            coordinator = get(),
            noteRepository = get(),
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