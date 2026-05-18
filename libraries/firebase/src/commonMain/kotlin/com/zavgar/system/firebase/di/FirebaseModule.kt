package com.zavgar.system.firebase.di

import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.firebase.config.RemoteConfigServiceImpl
import com.zavgar.system.firebase.crash.CrashReporter
import com.zavgar.system.firebase.crash.CrashReporterImpl
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Имя Koin-квалификатора для флага debug-сборки.
 *
 * Значение этого qualifier'а предоставляется в `shared` при инициализации Koin.
 */
const val IS_DEBUG_BUILD: String = "isDebugBuild"

val firebaseModule = module {

    single {
        RemoteConfigServiceImpl(isDebugBuild = get(named(IS_DEBUG_BUILD)))
    } bind RemoteConfigService::class

    single<CrashReporter> { CrashReporterImpl() }
}
