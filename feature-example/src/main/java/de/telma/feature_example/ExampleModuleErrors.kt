package de.telma.feature_example

import de.telma.todolist.core_ui.state.BaseUiError

sealed interface ExampleModuleErrors: BaseUiError {
    data object GenericError: ExampleModuleErrors
}