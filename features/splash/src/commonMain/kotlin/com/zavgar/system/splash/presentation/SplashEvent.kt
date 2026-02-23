package com.zavgar.system.splash.presentation

sealed interface SplashEvent {
    data object NavigateToLogin : SplashEvent
    data object NavigateToWallet : SplashEvent
}