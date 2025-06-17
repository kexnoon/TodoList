package de.telma.feature_example.di

import de.telma.feature_example.FeatureExampleNavigator
import de.telma.feature_example.dummy_screen_1.DummyScreenOneViewModel
import de.telma.feature_example.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.feature_example.dummy_screen_3.DummyScreenThreeViewModel
import de.telma.feature_example.main_screen.MainScreenViewModel
import de.telma.todolist.core_ui.navigation.NavigationCoordinator
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureExampleModule = module {
    single { FeatureExampleNavigator() }
    viewModel { MainScreenViewModel(get(), get()) }
    viewModel { DummyScreenOneViewModel(get()) }
    viewModel { DummyScreenTwoViewModel(get()) }
    viewModel { DummyScreenThreeViewModel(get()) }
}