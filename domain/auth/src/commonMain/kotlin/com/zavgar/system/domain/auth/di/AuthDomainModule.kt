package com.zavgar.system.domain.auth.di

import com.zavgar.system.domain.auth.usecase.ConfirmationUseCase
import com.zavgar.system.domain.auth.usecase.LoginUseCase
import com.zavgar.system.domain.auth.usecase.RegisterUseCase
import com.zavgar.system.domain.auth.usecase.ResendCodeUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val authDomainModule = module {
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::ConfirmationUseCase)
    factoryOf(::ResendCodeUseCase)
}
