package com.zavgar.system.settings.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.datastore.datasource.ThemeDataSource
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.domain.userinfo.usecase.LogoutUseCase
import com.zavgar.system.settings.mapper.asSnackBarMessage
import com.zavgar.system.settings.mapper.toLogoutResult
import com.zavgar.system.settings.model.LogoutResult
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserBalanceUseCase: GetUserBalanceUseCase,
    private val themeDataSource: ThemeDataSource,
) : BaseViewModel<SettingsState, SettingsIntent, SettingsEvent>(SettingsState()) {

    init {
        loadData()
    }

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ToProfileDetail -> handleToProfileDetail()
            is SettingsIntent.Logout -> handleLogout()
            is SettingsIntent.Retry -> loadData()
            is SettingsIntent.ToggleDarkMode -> handleToggleDarkMode(intent.isDark)
        }
    }

    private fun loadData() {
        launchTry {
            setState { copy(profileState = SettingsState.ProfileState.Loading) }
            coroutineScope {
                val profileDeferred = async { getUserProfileUseCase() }
                val balanceDeferred = async { getUserBalanceUseCase() }

                val isDarkTheme = themeDataSource.isDarkTheme.first()
                setState { copy(isDarkTheme = isDarkTheme) }

                val profileResult = profileDeferred.await()
                val balanceResult = balanceDeferred.await()

                when {
                    profileResult is AppResult.Success && balanceResult is AppResult.Success -> {
                        setState {
                            copy(
                                profileState = SettingsState.ProfileState.Content(
                                    name = profileResult.data.name,
                                    phone = profileResult.data.phone,
                                    balance = balanceResult.data.balance,
                                )
                            )
                        }
                    }

                    else -> setState { copy(profileState = SettingsState.ProfileState.Error) }
                }
            }
        } catch {
            setState { copy(profileState = SettingsState.ProfileState.Error) }
            setEvent {
                SettingsEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun handleToggleDarkMode(isDark: Boolean) {
        launchTry {
            themeDataSource.setDarkTheme(isDark)
            setState { copy(isDarkTheme = isDark) }
        } catch {
            // ignore — UI remains in previous state
        }
    }

    private fun handleLogout() {
        if (currentState.isLoggingOut) return
        launchTry {
            setState { copy(isLoggingOut = true) }
            val result = logoutUseCase().toLogoutResult { it.asSnackBarMessage() }
            when (result) {
                is LogoutResult.Success -> setEvent { SettingsEvent.NavigateToLogin }
                is LogoutResult.Error -> {
                    setState { copy(isLoggingOut = false) }
                    setEvent { SettingsEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(isLoggingOut = false) }
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
