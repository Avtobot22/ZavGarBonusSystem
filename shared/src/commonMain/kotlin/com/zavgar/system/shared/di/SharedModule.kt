package com.zavgar.system.shared.di

import com.zavgar.system.domain.session.LogoutNotifier
import com.zavgar.system.shared.session.AppEventBusLogoutNotifier
import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

val sharedModule = module {
    includes(platformSharedModule)

    single<Clock> { Clock.System }

    // Реализация доменного порта LogoutNotifier поверх AppEventBus (композиционный слой).
    single { AppEventBusLogoutNotifier(get()) } bind LogoutNotifier::class
}

/**
 * Provides the platform-specific dependencies.
 */
internal expect val platformSharedModule: Module
