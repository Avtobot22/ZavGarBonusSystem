package com.zavgar.system.navigation.di

import com.zavgar.system.navigation.controller.NavEventControllerImpl
import com.zavgar.system.navigation.provider.NavGraphProvider
import com.zavgar.system.navigationapi.controller.NavEventController
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val navigationModule = module {

    singleOf(::NavEventControllerImpl) bind NavEventController::class
    single { NavGraphProvider(get(), getAll()) }
}
