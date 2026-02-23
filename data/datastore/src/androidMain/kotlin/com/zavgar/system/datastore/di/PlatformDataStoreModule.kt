package com.zavgar.system.datastore.di

import com.zavgar.system.datastore.AndroidDataStore
import org.koin.dsl.module

/**
 * Koin module to provide the DataStore implementation.
 */
internal actual val platformDataStoreModule = module {
    single { AndroidDataStore(context = get()).getDataStore() }
}
