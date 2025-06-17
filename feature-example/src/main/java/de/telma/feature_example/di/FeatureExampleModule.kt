package de.telma.feature_example.di

import de.telma.feature_example.dummy_screen_1.DummyScreenOneViewModel
import de.telma.feature_example.dummy_screen_2.DummyScreenTwoViewModel
import de.telma.feature_example.dummy_screen_3.DummyScreenThreeViewModel
import de.telma.feature_example.main_screen.MainScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureExampleModule = module {
    viewModel { MainScreenViewModel(get(), get()) }
    viewModel { DummyScreenOneViewModel(get()) }
    viewModel { DummyScreenTwoViewModel(get()) }
    viewModel { DummyScreenThreeViewModel(get()) }
}