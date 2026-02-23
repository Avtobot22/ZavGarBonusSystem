package com.zavgar.system.datastore.di

import com.zavgar.system.datastore.datasource.SessionDataSourceImpl
import com.zavgar.system.repository.datasource.SessionDataSource
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * DataStore dependency injection module.
 */
val dataStoreModule = module {

    // Data Source
    singleOf(::SessionDataSourceImpl) bind SessionDataSource::class

    includes(platformDataStoreModule)
}

/**
 * Provides the platform-specific dependencies.
 */
internal expect val platformDataStoreModule: Module
