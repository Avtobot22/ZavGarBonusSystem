package com.zavgar.system.settings.presentation

import com.zavgar.system.core.presentation.util.SnackBarMessage

sealed interface SettingsEvent {

    data object NavigateToProfileDetail : SettingsEvent

    data object NavigateToLogin : SettingsEvent

    data class ShowSnackbar(val message: SnackBarMessage) : SettingsEvent
}
