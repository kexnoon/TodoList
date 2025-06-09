package de.telma.todolist.data.di

import androidx.room.Room
import de.telma.todolist.data.NoteRepository
import de.telma.todolist.data.NoteRepositoryImpl
import de.telma.todolist.data.database.AppDatabase
import org.koin.dsl.module

val dataModule = module {
    single<AppDatabase> {
        Room
            .databaseBuilder(
                context = get(),
                klass = AppDatabase::class.java,
                name = AppDatabase.DATABASE_NAME
            )
            .createFromAsset("${AppDatabase.DATABASE_NAME}.db")
            .build()
    }

    factory<NoteRepository> { NoteRepositoryImpl(get()) }
}