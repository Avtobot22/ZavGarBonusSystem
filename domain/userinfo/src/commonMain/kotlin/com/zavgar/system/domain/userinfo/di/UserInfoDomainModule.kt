package com.zavgar.system.domain.userinfo.di

import com.zavgar.system.domain.userinfo.usecase.DeleteUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.GetCachedBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetMonthlyAccrualsUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.LogoutUseCase
import com.zavgar.system.domain.userinfo.usecase.UpdateUserProfileUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val userInfoDomainModule = module {
    factoryOf(::GetUserProfileUseCase)
    factoryOf(::GetUserBalanceUseCase)
    factoryOf(::GetCachedBalanceUseCase)
    factoryOf(::GetMonthlyAccrualsUseCase)
    factoryOf(::UpdateUserProfileUseCase)
    factoryOf(::DeleteUserProfileUseCase)
    factoryOf(::LogoutUseCase)
}
