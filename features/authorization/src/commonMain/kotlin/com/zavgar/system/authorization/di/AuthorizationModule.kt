package com.zavgar.system.authorization.di

import com.zavgar.system.authorization.domain.usecase.LoginUseCase
import com.zavgar.system.authorization.navigation.LoginNavGraph
import com.zavgar.system.authorization.presentation.LoginViewModel
import com.zavgar.system.navigationapi.provider.NavGraph
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authorizationModule = module {

    factoryOf(::LoginUseCase)

    viewModelOf(::LoginViewModel)

    factoryOf(::LoginNavGraph) bind NavGraph::class

}