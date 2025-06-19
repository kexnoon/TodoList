package de.telma.todolist

import android.app.Application
import de.telma.todolist.component_notes.di.componentNotesModule
import de.telma.todolist.storage.database.di.storageModule
import de.telma.todolist.feature_main.di.featureMainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                storageModule,
                componentNotesModule,
                featureMainModule
            )
        }
    }
}