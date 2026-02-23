package com.zavgar.system.shared.di

import com.zavgar.system.shared.AppViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val sharedModule = module {

    viewModelOf(::AppViewModel)
    includes(platformSharedModule)
}

/**
 * Provides the platform-specific dependencies.
 */
internal expect val platformSharedModule: Module