package com.zavgar.system.settings.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.settings.domain.usecase.LogoutUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.settings.mapper.asSnackBarMessage
import com.zavgar.system.settings.mapper.toLogoutResult
import com.zavgar.system.settings.model.LogoutResult

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
        launchTry {
            setState { copy(screenState = SettingsState.ScreenState.Loading) }
            val result = logoutUseCase().toLogoutResult { it.asSnackBarMessage() }
            when (result) {
                is LogoutResult.Success -> setEvent { SettingsEvent.NavigateToLogin }
                is LogoutResult.Error -> {
                    setState { copy(screenState = SettingsState.ScreenState.Content) }
                    setEvent { SettingsEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(screenState = SettingsState.ScreenState.Content) }
            setEvent {
                SettingsEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun handleToProfileDetail() {
        setEvent { SettingsEvent.NavigateToProfileDetail }
    }
}
