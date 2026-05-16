package com.zavgar.system.onboarding.presentation

sealed interface OnboardingIntent {
    data object Finish : OnboardingIntent
}
