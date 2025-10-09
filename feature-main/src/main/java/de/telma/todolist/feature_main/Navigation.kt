package de.telma.todolist.feature_main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import de.telma.todolist.core_ui.navigation.Destination
import de.telma.todolist.feature_main.main_screen.MainScreen
import de.telma.todolist.feature_main.note_screen.NoteScreen
import de.telma.todolist.feature_main.note_screen.NoteScreenViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.featureMain() {
    mainScreen()
    noteScreen()
}

internal fun NavGraphBuilder.mainScreen() {
    composable<MainDestination.MainScreen> {
        MainScreen(viewModel = koinViewModel())
    }
}

internal fun NavGraphBuilder.noteScreen() {
    composable<MainDestination.NoteScreen> {
        val args = it.toRoute<MainDestination.NoteScreen>()
        val viewModel = koinViewModel<NoteScreenViewModel> {
            parametersOf(args.noteId)
        }
        NoteScreen(viewModel)
    }
}

sealed class MainDestination : Destination {
    @Serializable
    data object MainScreen : MainDestination()

    @Serializable
    data class NoteScreen(val noteId: Long) : MainDestination()
}