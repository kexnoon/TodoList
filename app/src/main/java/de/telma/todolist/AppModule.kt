package de.telma.todolist

import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import org.koin.dsl.module
import java.time.Clock

internal val appModule = module {
    single { NavigationCoordinator() }
    factory { Clock.systemUTC() }
}