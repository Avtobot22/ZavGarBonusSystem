package com.zavgar.system.shared.di

import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.time.Clock

val sharedModule = module {
    includes(platformSharedModule)

    single<Clock> { Clock.System }
}

/**
 * Provides the platform-specific dependencies.
 */
internal expect val platformSharedModule: Module
