package de.telma.todolist

import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import org.koin.dsl.module

internal val appModule = module {
    single { NavigationCoordinator() }
}