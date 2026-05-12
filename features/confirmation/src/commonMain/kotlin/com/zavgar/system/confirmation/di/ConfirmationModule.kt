package com.zavgar.system.confirmation.di

import com.zavgar.system.confirmation.navigation.ConfirmationNavGraph
import com.zavgar.system.confirmation.presentation.ConfirmationViewModel
import com.zavgar.system.navigationapi.provider.NavGraph
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val confirmationModule = module {

    viewModelOf(::ConfirmationViewModel)

    factoryOf(::ConfirmationNavGraph) bind NavGraph::class

}