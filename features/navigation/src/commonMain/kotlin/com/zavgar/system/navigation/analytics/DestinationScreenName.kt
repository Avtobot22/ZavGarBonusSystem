package com.zavgar.system.navigation.analytics

import com.zavgar.system.navigationapi.destination.AuthDestination
import com.zavgar.system.navigationapi.destination.Destination
import com.zavgar.system.navigationapi.destination.HomeDestination
import com.zavgar.system.navigationapi.destination.OnboardingDestination
import com.zavgar.system.navigationapi.destination.SettingsDestination
import com.zavgar.system.navigationapi.destination.SplashDestination

/**
 * Имя экрана для события `screen_view`. Возвращает null для служебных
 * назначений (например [Destination.Back]), которые не считаются экранами.
 */
internal fun Destination.screenName(): String? = when (this) {
    SplashDestination -> "splash"
    OnboardingDestination -> "onboarding"
    AuthDestination.Login -> "login"
    AuthDestination.Register -> "register"
    is AuthDestination.Confirmation -> "confirmation"
    HomeDestination.Wallet -> "wallet"
    HomeDestination.History -> "history"
    HomeDestination.Settings -> "settings"
    SettingsDestination.Profile -> "profile"
    else -> null
}
