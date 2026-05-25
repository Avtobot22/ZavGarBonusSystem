package com.zavgar.system.shared.di

import com.zavgar.system.account.di.accountModule
import com.zavgar.system.authorization.di.authorizationModule
import com.zavgar.system.confirmation.di.confirmationModule
import com.zavgar.system.coroutines.di.coroutinesModule
import com.zavgar.system.datastore.di.dataStoreModule
import com.zavgar.system.designsystem.di.designSystemModule
import com.zavgar.system.domain.auth.di.authDomainModule
import com.zavgar.system.domain.onboarding.di.onboardingDomainModule
import com.zavgar.system.domain.operations.di.operationsDomainModule
import com.zavgar.system.domain.session.di.sessionModule
import com.zavgar.system.domain.theme.di.themeDomainModule
import com.zavgar.system.domain.userinfo.di.userInfoDomainModule
import com.zavgar.system.events.di.eventsModule
import com.zavgar.system.firebase.di.IS_DEBUG_BUILD
import com.zavgar.system.firebase.di.firebaseModule
import com.zavgar.system.history.di.historyModule
import com.zavgar.system.navigation.di.navigationModule
import com.zavgar.system.onboarding.di.onboardingModule
import com.zavgar.system.network.di.networkModule
import com.zavgar.system.repository.di.repositoryModule
import com.zavgar.system.registration.di.registrationModule
import com.zavgar.system.settings.di.settingsModule
import com.zavgar.system.splash.di.splashModule
import com.zavgar.system.utils.validation.di.validationModule
import com.zavgar.system.wallet.di.walletModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Initializes the Koin modules with the platform default debug flag.
 */
fun initKoin() {
    initKoin(isDebugBuild = isDebugBuildDefault)
}

/**
 * Initializes the Koin modules.
 *
 * @param isDebugBuild whether the build is a debug build (drives verbose network logging)
 * @param appModule the app module to be included
 */
fun initKoin(isDebugBuild: Boolean = isDebugBuildDefault, appModule: Module = module { }) {
    startKoin {
        modules(appModules + appModule + buildEnvironmentModule(isDebugBuild))
    }
}

private fun buildEnvironmentModule(isDebugBuild: Boolean): Module = module {
    single(named(IS_DEBUG_BUILD)) { isDebugBuild }
}

internal val appModules = listOf(
    // Infrastructure
    sharedModule,
    coroutinesModule,
    eventsModule,
    designSystemModule,
    firebaseModule,

    // Data
    dataStoreModule,
    networkModule,
    repositoryModule,

    // Domain
    sessionModule,
    authDomainModule,
    operationsDomainModule,
    userInfoDomainModule,
    themeDomainModule,
    onboardingDomainModule,
    validationModule,

    // Navigation
    navigationModule,

    // Features
    authorizationModule,
    registrationModule,
    confirmationModule,
    splashModule,
    onboardingModule,
    walletModule,
    settingsModule,
    accountModule,
    historyModule
)