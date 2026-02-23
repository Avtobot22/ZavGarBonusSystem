package com.zavgar.system.registration.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.registration.navigation.RegisterNavGraph
import com.zavgar.system.registration.presentation.RegisterViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val registrationModule = module {
    viewModelOf(::RegisterViewModel)

    factoryOf(::RegisterNavGraph) bind NavGraph::class
}