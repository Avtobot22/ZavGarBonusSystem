package com.zavgar.system.onboarding.presentation

sealed interface OnboardingEvent {
    data object NavigateToLogin : OnboardingEvent
}
