package com.zavgar.system.datastore.di

import com.zavgar.system.datastore.AndroidDataStore
import com.zavgar.system.datastore.datasource.AndroidSecureTokenStorage
import com.zavgar.system.datastore.datasource.SecureTokenStorage
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Koin module to provide the DataStore implementation.
 */
internal actual val platformDataStoreModule = module {
    single { AndroidDataStore(context = get()).getDataStore() }
    single { AndroidSecureTokenStorage(context = get()) } bind SecureTokenStorage::class
}
