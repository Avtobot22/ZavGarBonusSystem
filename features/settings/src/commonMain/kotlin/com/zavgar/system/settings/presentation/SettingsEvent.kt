package com.zavgar.system.settings.presentation

import com.zavgar.system.core.presentation.util.UiText

sealed interface SettingsEvent {

    data object NavigateToProfileDetail : SettingsEvent

    data object NavigateToAboutApp : SettingsEvent

    data object NavigateToLogin : SettingsEvent

    data class ShowSnackbar(val message: UiText) : SettingsEvent
}