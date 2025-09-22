package de.telma.todolist.feature_main.di

import de.telma.todolist.feature_main.main_screen.MainScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureMainModule = module {
    viewModel {
        MainScreenViewModel(
            coordinator = get(),
            repository = get(),
            createNewNoteUseCase = get(),
            deleteNotesUseCase = get())
    }
}