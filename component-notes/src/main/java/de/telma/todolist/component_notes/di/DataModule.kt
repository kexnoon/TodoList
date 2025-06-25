package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.NoteRepository
import de.telma.todolist.component_notes.NoteRepositoryImpl
import de.telma.todolist.component_notes.RenameNoteUseCase
import de.telma.todolist.component_notes.TaskRepository
import de.telma.todolist.component_notes.TaskRepositoryImpl
import de.telma.todolist.component_notes.UpdateNoteStatusUseCase
import org.koin.dsl.module

val componentNotesModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<TaskRepository> { TaskRepositoryImpl(get()) }
    factory<RenameNoteUseCase> { RenameNoteUseCase(get()) }
    factory<UpdateNoteStatusUseCase> { UpdateNoteStatusUseCase(get()) }
}