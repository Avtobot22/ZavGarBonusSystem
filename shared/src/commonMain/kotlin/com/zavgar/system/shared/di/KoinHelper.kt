package com.zavgar.system.shared.di

import com.zavgar.system.account.di.accountModule
import com.zavgar.system.authorization.di.authorizationModule
import com.zavgar.system.confirmation.di.confirmationModule
import com.zavgar.system.coroutines.di.coroutinesModule
import com.zavgar.system.datastore.di.dataStoreModule
import com.zavgar.system.designsystem.di.designSystemModule
import com.zavgar.system.domain.di.domainModule
import com.zavgar.system.history.di.historyModule
import com.zavgar.system.navigation.di.navigationModule
import com.zavgar.system.network.di.networkModule
import com.zavgar.system.registration.di.registrationModule
import com.zavgar.system.repository.di.repositoryModule
import com.zavgar.system.resetpassword.di.resetPasswordModule
import com.zavgar.system.settings.di.settingsModule
import com.zavgar.system.splash.di.splashModule
import com.zavgar.system.wallet.di.walletModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Initializes the Koin modules.
 */
fun initKoin() {
    initKoin(module { })
}

/**
 * Initializes the Koin modules.
 *
 * @param appModule the app module to be included
 */
fun initKoin(appModule: Module = module { }) {
    startKoin {
        modules(appModules + appModule)
    }
}

// TODO Все модули приложения
internal val appModules = listOf(
    sharedModule,
    coroutinesModule,
    designSystemModule,
    networkModule,
    repositoryModule,
    dataStoreModule,
    domainModule,
    authorizationModule,
    navigationModule,
    registrationModule,
    confirmationModule,
    resetPasswordModule,
    splashModule,
    walletModule,
    settingsModule,
    accountModule,
    historyModule
)