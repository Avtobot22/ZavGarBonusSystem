package com.zavgar.system.repository.di

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.onboarding.repository.OnboardingRepository
import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.session.repository.SessionRepository
import com.zavgar.system.domain.theme.repository.ThemeRepository
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.repository.AuthRepositoryImpl
import com.zavgar.system.repository.OnboardingRepositoryImpl
import com.zavgar.system.repository.OperationsRepositoryImpl
import com.zavgar.system.repository.ProfileRepositoryImpl
import com.zavgar.system.repository.SessionRepositoryImpl
import com.zavgar.system.repository.ThemeRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::OperationsRepositoryImpl) bind OperationsRepository::class
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
    singleOf(::SessionRepositoryImpl) bind SessionRepository::class
    singleOf(::ThemeRepositoryImpl) bind ThemeRepository::class
    singleOf(::OnboardingRepositoryImpl) bind OnboardingRepository::class
}
