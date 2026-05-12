package com.zavgar.system.domain.operations.di

import com.zavgar.system.domain.operations.usecase.GetOperationsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val operationsDomainModule = module {
    factoryOf(::GetOperationsUseCase)
}
