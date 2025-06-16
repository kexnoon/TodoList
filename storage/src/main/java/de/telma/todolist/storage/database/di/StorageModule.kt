package de.telma.todolist.storage.database.di

import androidx.room.Room
import de.telma.todolist.storage.database.AppDatabase
import org.koin.dsl.module

val storageModule = module {
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

}