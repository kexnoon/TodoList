package de.telma.todolist.component_notes.base

interface BaseUseCase<T> {
    fun execute(): T
}