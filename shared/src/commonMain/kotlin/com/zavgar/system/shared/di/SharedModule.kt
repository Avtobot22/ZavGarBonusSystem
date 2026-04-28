package com.zavgar.system.shared.di

import org.koin.core.module.Module
import org.koin.dsl.module

val sharedModule = module {
    includes(platformSharedModule)
}

/**
 * Provides the platform-specific dependencies.
 */
internal expect val platformSharedModule: Module
