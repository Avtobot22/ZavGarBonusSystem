package com.zavgar.system.settings.presentation

sealed interface SettingsIntent {

    data object Logout : SettingsIntent

    data object ToProfileDetail : SettingsIntent
}