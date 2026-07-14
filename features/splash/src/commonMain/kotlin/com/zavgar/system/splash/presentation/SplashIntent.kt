package com.zavgar.system.splash.presentation

sealed interface SplashIntent {
    data object ScreenEntered : SplashIntent
}
