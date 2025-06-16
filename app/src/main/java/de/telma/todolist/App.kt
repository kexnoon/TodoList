package de.telma.todolist

import android.app.Application
import de.telma.todolist.data.di.dataModule
import de.telma.todolist.storage.database.di.storageModule
import de.telma.todolist.ui.di.uiModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                storageModule,
                dataModule,
                uiModule
            )
        }
    }
}