package de.telma.todolist.ui.di

import de.telma.todolist.ui.dummy_screen_1.DummyScreenOneViewModel
import de.telma.todolist.ui.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.todolist.ui.main_screen.MainScreenViewModel
import de.telma.todolist.ui.navigation.NavigationCoordinator
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {
    single { NavigationCoordinator() }
    viewModel { MainScreenViewModel(get(), get()) }
    viewModel { DummyScreenOneViewModel(get()) }
    viewModel { DummyScreenTwoViewModel(get()) }
}