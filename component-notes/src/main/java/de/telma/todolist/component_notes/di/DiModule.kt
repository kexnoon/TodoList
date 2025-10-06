package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.useCase.RenameNoteUseCase
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.DeleteMultipleNotesUseCase
import de.telma.todolist.component_notes.useCase.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.UpdateTaskStatusUseCase
import org.koin.dsl.module
import java.time.Clock

val componentNotesModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<TaskRepository> { TaskRepositoryImpl(get()) }
    factory<Clock> { Clock.systemUTC() }

    factory { RenameNoteUseCase(get(), get()) }
    factory { UpdateNoteStatusUseCase(get(), get()) }
    factory { RenameTaskUseCase(get(), get(), get()) }
    factory { UpdateTaskStatusUseCase(get(), get(), get()) }
    factory { SyncNoteStatusUseCase(get()) }
    factory { DeleteMultipleNotesUseCase(get()) }
    factory { CreateNewTaskUseCase(get(), get(), get()) }
    factory { CreateNewNoteUseCase(get(), get()) }
    factory { DeleteNoteUseCase(get()) }
    factory { DeleteTaskUseCase(get()) }
}