package de.telma.todolist.architecture_example

import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import org.koin.dsl.module

internal val appExampleModule = module {
    single { NavigationCoordinator() }
}