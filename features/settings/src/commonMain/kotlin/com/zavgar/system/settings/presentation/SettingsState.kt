package com.zavgar.system.settings.presentation

sealed interface SettingsState {
    data object Content : SettingsState
    data object Loading : SettingsState
}