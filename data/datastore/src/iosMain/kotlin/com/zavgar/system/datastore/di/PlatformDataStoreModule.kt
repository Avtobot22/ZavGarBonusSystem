package com.zavgar.system.datastore.di

import com.zavgar.system.datastore.IosDataStore
import com.zavgar.system.datastore.datasource.IosSecureTokenStorage
import com.zavgar.system.datastore.datasource.SecureTokenStorage
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual val platformDataStoreModule = module {
    single { IosDataStore().getDataStore() }
    single { IosSecureTokenStorage() } bind SecureTokenStorage::class
}
