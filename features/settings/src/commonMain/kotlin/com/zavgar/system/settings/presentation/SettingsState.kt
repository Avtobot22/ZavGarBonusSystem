package com.zavgar.system.settings.presentation

data class SettingsState(
    val screenState: ScreenState = ScreenState.Loading,
) {
    sealed interface ScreenState {
        data class Content(
            val name: String,
            val phone: String,
            val balance: Int,
            val isDarkTheme: Boolean = false,
        ) : ScreenState
        data object Loading : ScreenState

        data object Error : ScreenState
    }
}
