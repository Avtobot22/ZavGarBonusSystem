package com.zavgar.system.settings.presentation

data class SettingsState(
    val screenState: ScreenState = ScreenState.Content,
) {
    sealed interface ScreenState {
        data object Content : ScreenState
        data object Loading : ScreenState
    }
}
