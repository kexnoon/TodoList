package de.telma.todolist.architecture_example

import android.app.Application
import de.telma.feature_example.di.featureExampleModule
import de.telma.todolist.component_notes.di.componentNotesModule
import de.telma.todolist.storage.database.di.storageModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appExampleModule,
                storageModule,
                componentNotesModule,
                featureExampleModule,
            )
        }
    }
}