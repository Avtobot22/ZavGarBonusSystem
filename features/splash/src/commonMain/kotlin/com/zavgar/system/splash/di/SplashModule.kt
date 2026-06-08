package com.zavgar.system.splash.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.splash.navigation.SplashNavGraph
import com.zavgar.system.splash.presentation.SplashViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    viewModelOf(::SplashViewModel)

    factoryOf(::SplashNavGraph) bind NavGraph::class
}
