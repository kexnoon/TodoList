package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.useCase.note.RenameNoteUseCase
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.task.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.task.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.task.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.note.UpdateNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.task.UpdateTaskStatusUseCase
import org.koin.dsl.module
import java.time.Clock

val componentNotesModule = module {
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<TaskRepository> { TaskRepositoryImpl(get()) }
    factory<Clock> { Clock.systemUTC() }

    factory { CreateNewNoteUseCase(get(), get()) }
    factory { RenameNoteUseCase(get(), get()) }
    factory { UpdateNoteStatusUseCase(get(), get()) }
    factory { DeleteNoteUseCase(get()) }

    factory { CreateNewTaskUseCase(get(), get(), get()) }
    factory { RenameTaskUseCase(get(), get(), get()) }
    factory { UpdateTaskStatusUseCase(get(), get(), get()) }
    factory { DeleteTaskUseCase(get()) }
    factory { SyncNoteStatusUseCase(get()) }
}