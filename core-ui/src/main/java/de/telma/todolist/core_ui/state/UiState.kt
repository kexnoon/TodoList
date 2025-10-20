package de.telma.todolist.core_ui.state

sealed class UiState<out T, out E> {
    class Loading(): UiState<Nothing, Nothing>()
    data class Result<T>(var data: T): UiState<T, Nothing>()
    data class Error<E>(val uiError: E): UiState<Nothing, E>()
}

fun <T> T.toUiState() = UiState.Result(this)
//fun <T: Throwable> T.toUiState() = UiState.Error(this.message ?: "Something went wrong!")