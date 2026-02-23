package com.zavgar.system.coroutines.di

import com.zavgar.system.coroutines.AppCoroutineScope
import com.zavgar.system.coroutines.CoroutineDebouncer
import com.zavgar.system.coroutines.CoroutineDebouncerImpl
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.coroutines.CoroutineDispatcherProviderImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Coroutines library dependency injection module.
 */
val coroutinesModule = module {

    single { AppCoroutineScope() }
    factoryOf(::CoroutineDebouncerImpl) bind CoroutineDebouncer::class
    factoryOf(::CoroutineDispatcherProviderImpl) bind CoroutineDispatcherProvider::class
}
