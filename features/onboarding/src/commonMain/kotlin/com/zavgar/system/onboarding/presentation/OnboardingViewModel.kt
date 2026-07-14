package com.zavgar.system.onboarding.presentation

import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.onboarding.usecase.CompleteOnboardingUseCase

class OnboardingViewModel(
    private val completeOnboardingUseCase: CompleteOnboardingUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<OnboardingState, OnboardingIntent, OnboardingEvent>(OnboardingState) {

    private var isScreenEntryLogged = false

    override fun handleIntent(intent: OnboardingIntent) {
        when (intent) {
            is OnboardingIntent.ScreenEntered -> handleScreenEntered()
            is OnboardingIntent.Finish -> finishOnboarding()
        }
    }

    private fun handleScreenEntered() {
        if (isScreenEntryLogged) return
        isScreenEntryLogged = true
        analyticsTracker.log(AnalyticsEvent.OnboardingStart)
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
