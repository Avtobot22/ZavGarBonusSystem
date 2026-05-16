package com.zavgar.system.onboarding.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.datastore.datasource.OnboardingDataSource

class OnboardingViewModel(
    private val onboardingDataSource: OnboardingDataSource,
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEvent>(OnboardingState) {

    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.Finish -> finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        launchTry {
            onboardingDataSource.setOnboardingCompleted()
            setEvent { OnboardingEvent.NavigateToLogin }
        } catch {
            setEvent { OnboardingEvent.NavigateToLogin }
        }
    }
}
