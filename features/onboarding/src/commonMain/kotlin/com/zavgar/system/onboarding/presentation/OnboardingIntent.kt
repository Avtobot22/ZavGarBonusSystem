package com.zavgar.system.onboarding.presentation

sealed interface OnboardingIntent {
    data object ScreenEntered : OnboardingIntent
    data object Finish : OnboardingIntent
}
