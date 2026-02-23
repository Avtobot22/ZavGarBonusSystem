package com.zavgar.system.datastore.di

import com.zavgar.system.datastore.IosDataStore
import org.koin.dsl.module

internal actual val platformDataStoreModule = module {
    single { IosDataStore().getDataStore() }
}
