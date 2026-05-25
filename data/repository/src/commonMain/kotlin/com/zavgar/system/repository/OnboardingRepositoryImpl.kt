package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.OnboardingDataSource
import com.zavgar.system.domain.onboarding.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class OnboardingRepositoryImpl(
    private val dataSource: OnboardingDataSource,
) : OnboardingRepository {

    override val isOnboardingCompleted: Flow<Boolean> = dataSource.isOnboardingCompleted

    override suspend fun setOnboardingCompleted() = dataSource.setOnboardingCompleted()
}
