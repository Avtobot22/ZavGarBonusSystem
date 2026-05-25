package com.zavgar.system.domain.onboarding.usecase

import com.zavgar.system.domain.onboarding.repository.OnboardingRepository

class CompleteOnboardingUseCase(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke() = onboardingRepository.setOnboardingCompleted()
}
