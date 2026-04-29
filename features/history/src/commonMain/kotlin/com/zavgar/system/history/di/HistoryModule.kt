package com.zavgar.system.history.di

import com.zavgar.system.history.domain.usecase.GetOperationsUseCase
import com.zavgar.system.history.navigation.HistoryNavGraph
import com.zavgar.system.history.presentation.HistoryViewModel
import com.zavgar.system.navigationapi.provider.NavGraph
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val historyModule = module {
    factoryOf(::GetOperationsUseCase)
    viewModelOf(::HistoryViewModel)

    factoryOf(::HistoryNavGraph) bind NavGraph::class
}