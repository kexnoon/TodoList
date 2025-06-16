package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.NoteRepository
import de.telma.todolist.component_notes.NoteRepositoryImpl
import org.koin.dsl.module

val componentNotesModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
}