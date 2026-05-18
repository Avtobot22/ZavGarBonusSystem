package com.zavgar.system.settings.presentation

data class SettingsState(
    val profileState: ProfileState = ProfileState.Loading,
    val isDarkTheme: Boolean = false,
    val isLoggingOut: Boolean = false,
) {
    sealed interface ProfileState {
        data class Content(
            val name: String,
            val phone: String,
            val balance: Int,
        ) : ProfileState

        data object Loading : ProfileState

        data object Error : ProfileState
    }
}
