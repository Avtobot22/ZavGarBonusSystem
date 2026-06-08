package com.zavgar.system.splash.presentation

sealed interface SplashEvent {
    data object NavigateToLogin : SplashEvent
    data object NavigateToWallet : SplashEvent
    data object NavigateToOnboarding : SplashEvent
}
