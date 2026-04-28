package com.zavgar.system.settings.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.usecase.LogoutUseCase
import com.zavgar.system.settings.mapper.asSnackBarMessage
import com.zavgar.system.settings.mapper.toLogoutResult
import com.zavgar.system.settings.model.LogoutResult
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
) : BaseViewModel<SettingsState, SettingsIntent, SettingsEvent>(SettingsState()) {

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToProfileDetail -> handleToProfileDetail()
            is SettingsIntent.Logout -> handleLogout()
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            setState { copy(screenState = SettingsState.ScreenState.Loading) }
            val result = logoutUseCase().toLogoutResult { it.asSnackBarMessage() }
            when (result) {
                is LogoutResult.Success -> setEvent { SettingsEvent.NavigateToLogin }
                is LogoutResult.Error -> {
                    setState { copy(screenState = SettingsState.ScreenState.Content) }
                    setEvent { SettingsEvent.ShowSnackbar(result.message) }
                }
            }
        }
    }

    private fun handleToProfileDetail() {
        setEvent { SettingsEvent.NavigateToProfileDetail }
    }
}
