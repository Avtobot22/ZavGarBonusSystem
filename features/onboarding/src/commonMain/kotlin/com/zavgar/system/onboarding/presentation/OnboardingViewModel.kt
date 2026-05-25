package com.zavgar.system.onboarding.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.onboarding.usecase.CompleteOnboardingUseCase
import com.zavgar.system.firebase.analytics.AnalyticsEvent
import com.zavgar.system.firebase.analytics.AnalyticsTracker

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEvent>(OnboardingState) {

    init {
        analyticsTracker.log(AnalyticsEvent.OnboardingStart)
    }

    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.Finish -> finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        analyticsTracker.log(AnalyticsEvent.OnboardingComplete)
        analyticsTracker.setUserProperty(AnalyticsTracker.PROPERTY_ONBOARDING_COMPLETED, "true")
        launchTry {
            completeOnboardingUseCase()
            setEvent { OnboardingEvent.NavigateToLogin }
        } catch {
            setEvent { OnboardingEvent.NavigateToLogin }
        }
    }
}
