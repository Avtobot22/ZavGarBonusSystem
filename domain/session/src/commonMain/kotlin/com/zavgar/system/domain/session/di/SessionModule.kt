package com.zavgar.system.domain.session.di

import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.domain.session.usecase.DeleteSessionUseCase
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sessionModule = module {
    factoryOf(::GetSessionUseCase)
    factoryOf(::DeleteSessionUseCase)
    singleOf(::LogoutHandler)
}
