package com.zavgar.system.repository.di

import com.zavgar.system.domain.repository.AuthRepository
import com.zavgar.system.domain.repository.DataSourceRepository
import com.zavgar.system.domain.repository.LoyaltyRepository
import com.zavgar.system.domain.repository.UserProfileRepository
import com.zavgar.system.repository.AuthRepositoryImpl
import com.zavgar.system.repository.DataSourceRepositoryImpl
import com.zavgar.system.repository.LoyaltyRepositoryImpl
import com.zavgar.system.repository.UserProfileRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModule = module {

    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    singleOf(::DataSourceRepositoryImpl) bind DataSourceRepository::class

    singleOf(::LoyaltyRepositoryImpl) bind LoyaltyRepository::class

    singleOf(::UserProfileRepositoryImpl) bind UserProfileRepository::class

}