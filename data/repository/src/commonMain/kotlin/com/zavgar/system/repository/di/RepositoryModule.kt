package com.zavgar.system.repository.di

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.userinfo.repository.ProfileRepository
import com.zavgar.system.repository.AuthRepositoryImpl
import com.zavgar.system.repository.OperationsRepositoryImpl
import com.zavgar.system.repository.ProfileRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::OperationsRepositoryImpl) bind OperationsRepository::class
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class
}
