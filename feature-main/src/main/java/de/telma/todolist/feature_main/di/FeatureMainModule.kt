package de.telma.todolist.feature_main.di

import de.telma.todolist.feature_main.FeatureMainNavigator
import org.koin.dsl.module

val featureMainModule = module {
    single { FeatureMainNavigator() }
}