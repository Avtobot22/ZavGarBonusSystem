package com.zavgar.system.settings.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.usecase.LogoutUseCase
import com.zavgar.system.settings.mapper.asUiText
import com.zavgar.system.settings.mapper.toLogoutResult
import com.zavgar.system.settings.model.LogoutResult
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,

    ) : BaseViewModel<SettingsState, SettingsIntent, SettingsEvent>(SettingsState.Content) {

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToProfileDetail -> handleToProfileDetail()
            is SettingsIntent.ToAboutApp -> handleToAboutApp()
            is SettingsIntent.Logout -> handleLogout()
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            setState { SettingsState.Loading }
            val result = logoutUseCase().toLogoutResult { it.asUiText() }
            when (result) {
                is LogoutResult.Success -> setEvent { SettingsEvent.NavigateToLogin }
                is LogoutResult.Error -> {
                    setState { SettingsState.Content }
                    setEvent {
                        SettingsEvent.ShowSnackbar(result.message)
                    }
                }
            }
        }
    }

    private fun handleToAboutApp() {
        setEvent { SettingsEvent.NavigateToAboutApp }
    }

    private fun handleToProfileDetail() {
        setEvent { SettingsEvent.NavigateToProfileDetail }
    }


}