package de.telma.todolist.data.di

import de.telma.todolist.data.NoteRepository
import de.telma.todolist.data.NoteRepositoryImpl
import org.koin.dsl.module

val dataModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
}