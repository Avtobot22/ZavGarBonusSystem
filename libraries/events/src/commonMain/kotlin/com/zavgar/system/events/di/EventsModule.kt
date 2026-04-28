package com.zavgar.system.events.di

import com.zavgar.system.events.AppEventBus
import com.zavgar.system.events.AppEventBusImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val eventsModule = module {
    singleOf(::AppEventBusImpl) bind AppEventBus::class
}
