package de.telma.todolist.core.ui.state

sealed class UiState<out T> {
    class Loading<Nothing>(): UiState<Nothing>()
    class Result<T>(var data: T): UiState<T>()
}

fun <T> T.toUiState() = UiState.Result(this)