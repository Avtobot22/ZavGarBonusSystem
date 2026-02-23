package com.zavgar.system.settings.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.settings.navigation.SettingsNavGraph
import com.zavgar.system.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {

    viewModelOf(::SettingsViewModel)

    factoryOf(::SettingsNavGraph) bind NavGraph::class

}