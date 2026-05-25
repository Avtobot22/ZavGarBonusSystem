package com.zavgar.system.domain.onboarding.usecase

import com.zavgar.system.domain.onboarding.repository.OnboardingRepository
import kotlinx.coroutines.flow.Flow

class ObserveOnboardingCompletedUseCase(
    private val onboardingRepository: OnboardingRepository
) {
    operator fun invoke(): Flow<Boolean> = onboardingRepository.isOnboardingCompleted
}
