package com.zavgar.system.domain.onboarding.repository

import kotlinx.coroutines.flow.Flow

interface OnboardingRepository {

    val isOnboardingCompleted: Flow<Boolean>

    suspend fun setOnboardingCompleted()
}
