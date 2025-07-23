package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.useCase.RenameNoteUseCase
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.DeleteMultipleNotesUseCase
import de.telma.todolist.component_notes.useCase.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateTaskStatusUseCase
import org.koin.dsl.module

val componentNotesModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<TaskRepository> { TaskRepositoryImpl(get()) }

    factory { RenameNoteUseCase(get()) }
    factory { UpdateNoteStatusUseCase(get()) }
    factory { RenameTaskUseCase(get()) }
    factory { UpdateTaskStatusUseCase(get()) }
    factory { SyncNoteStatusUseCase(get()) }
    factory { DeleteMultipleNotesUseCase(get()) }
}