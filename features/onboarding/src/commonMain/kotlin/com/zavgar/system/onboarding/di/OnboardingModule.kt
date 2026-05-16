package com.zavgar.system.onboarding.di

import com.zavgar.system.navigationapi.provider.NavGraph
import com.zavgar.system.onboarding.navigation.OnboardingNavGraph
import com.zavgar.system.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)

    factoryOf(::OnboardingNavGraph) bind NavGraph::class
}
