package com.zavgar.system.domain.userinfo.di

import com.zavgar.system.domain.userinfo.usecase.ChangePasswordUseCase
import com.zavgar.system.domain.userinfo.usecase.DeleteUserProfileUseCase
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
    factoryOf(::GetMonthlyAccrualsUseCase)
    factoryOf(::UpdateUserProfileUseCase)
    factoryOf(::DeleteUserProfileUseCase)
    factoryOf(::ChangePasswordUseCase)
    factoryOf(::LogoutUseCase)
}
