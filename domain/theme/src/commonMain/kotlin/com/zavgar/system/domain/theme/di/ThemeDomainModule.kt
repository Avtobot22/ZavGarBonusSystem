package com.zavgar.system.domain.theme.di

import com.zavgar.system.domain.theme.usecase.ObserveDarkThemeUseCase
import com.zavgar.system.domain.theme.usecase.SetDarkThemeUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val themeDomainModule = module {
    factoryOf(::ObserveDarkThemeUseCase)
    factoryOf(::SetDarkThemeUseCase)
}
