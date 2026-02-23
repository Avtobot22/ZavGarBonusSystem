package com.zavgar.system.account.di

import com.zavgar.system.account.navigation.AccountNavGraph
import com.zavgar.system.account.presentation.AccountViewModel
import com.zavgar.system.navigationapi.provider.NavGraph
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val accountModule = module {

    viewModelOf(::AccountViewModel)

    factoryOf(::AccountNavGraph) bind NavGraph::class

}