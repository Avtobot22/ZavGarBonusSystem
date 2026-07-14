package com.zavgar.system.settings.presentation

sealed interface SettingsIntent {

    data object ScreenEntered : SettingsIntent

    data object Logout : SettingsIntent

    data object ToProfileDetail : SettingsIntent

    data object Retry : SettingsIntent

    data class ToggleDarkMode(val isDark: Boolean) : SettingsIntent
}
