package com.zavgar.system.domain.onboarding.di

import com.zavgar.system.domain.onboarding.usecase.CompleteOnboardingUseCase
import com.zavgar.system.domain.onboarding.usecase.ObserveOnboardingCompletedUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val onboardingDomainModule = module {
    factoryOf(::ObserveOnboardingCompletedUseCase)
    factoryOf(::CompleteOnboardingUseCase)
}
