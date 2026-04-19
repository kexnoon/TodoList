package de.telma.todolist.component_notes.di

import de.telma.todolist.component_notes.repository.FolderRepository
import de.telma.todolist.component_notes.repository.FolderRepositoryImpl
import de.telma.todolist.component_notes.repository.NoteRepository
import de.telma.todolist.component_notes.repository.NoteRepositoryImpl
import de.telma.todolist.component_notes.useCase.note.RenameNoteUseCase
import de.telma.todolist.component_notes.repository.TaskRepository
import de.telma.todolist.component_notes.repository.TaskRepositoryImpl
import de.telma.todolist.component_notes.useCase.folder.CreateFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.DeleteFolderUseCase
import de.telma.todolist.component_notes.useCase.folder.GetFoldersUseCase
import de.telma.todolist.component_notes.useCase.folder.RenameFolderUseCase
import de.telma.todolist.component_notes.useCase.note.CreateNewNoteUseCase
import de.telma.todolist.component_notes.useCase.task.CreateNewTaskUseCase
import de.telma.todolist.component_notes.useCase.note.DeleteNoteUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesInFolderUseCase
import de.telma.todolist.component_notes.useCase.note.GetNotesUseCase
import de.telma.todolist.component_notes.useCase.note.MoveNotesToFolderUseCase
import de.telma.todolist.component_notes.useCase.note.SetNoteFolderUseCase
import de.telma.todolist.component_notes.useCase.task.DeleteTaskUseCase
import de.telma.todolist.component_notes.useCase.task.RenameTaskUseCase
import de.telma.todolist.component_notes.useCase.note.SyncNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.note.UpdateNoteStatusUseCase
import de.telma.todolist.component_notes.useCase.task.UpdateTaskStatusUseCase
import org.koin.dsl.module
import java.time.Clock

val componentNotesModule = module {
    factory<FolderRepository> { FolderRepositoryImpl(get()) }
    factory<NoteRepository> { NoteRepositoryImpl(get()) }
    factory<TaskRepository> { TaskRepositoryImpl(get()) }
    factory<Clock> { Clock.systemUTC() }

    factory { GetFoldersUseCase(get()) }
    factory { CreateFolderUseCase(get(), get()) }
    factory { RenameFolderUseCase(get(), get()) }
    factory { DeleteFolderUseCase(get()) }

    factory { GetNotesUseCase(get()) }
    factory { GetNotesInFolderUseCase(get()) }
    factory { MoveNotesToFolderUseCase(get(), get(), get()) }
    factory { SetNoteFolderUseCase(get(), get(), get()) }
    factory { CreateNewNoteUseCase(get(), get(), get()) }
    factory { RenameNoteUseCase(get(), get(), get()) }
    factory { UpdateNoteStatusUseCase(get(), get()) }
    factory { DeleteNoteUseCase(get()) }

    factory { CreateNewTaskUseCase(get(), get(), get(), get()) }
    factory { RenameTaskUseCase(get(), get(), get(), get()) }
    factory { UpdateTaskStatusUseCase(get(), get(), get(), get()) }
    factory { DeleteTaskUseCase(get(), get(), get(), get()) }
    factory { SyncNoteStatusUseCase(get(), get(), get()) }
}
