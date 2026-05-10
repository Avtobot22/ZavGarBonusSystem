package com.zavgar.system.settings.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.datastore.datasource.ThemeDataSource
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.settings.domain.usecase.LogoutUseCase
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
            setState { copy(screenState = SettingsState.ScreenState.Loading) }
            coroutineScope {
                val profileDeferred = async { getUserProfileUseCase() }
                val balanceDeferred = async { getUserBalanceUseCase() }
                val isDarkTheme = themeDataSource.isDarkTheme.first()

                val profileResult = profileDeferred.await()
                val balanceResult = balanceDeferred.await()

                when {
                    profileResult is AppResult.Success && balanceResult is AppResult.Success -> {
                        setState {
                            copy(
                                screenState = SettingsState.ScreenState.Content(
                                    name = profileResult.data.name,
                                    phone = profileResult.data.phone,
                                    balance = balanceResult.data.balance,
                                    isDarkTheme = isDarkTheme,
                                )
                            )
                        }
                    }

                    else -> setState { copy(screenState = SettingsState.ScreenState.Error) }
                }
            }
        } catch {
            setState { copy(screenState = SettingsState.ScreenState.Error) }
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
            val current = state.value.screenState
            if (current is SettingsState.ScreenState.Content) {
                setState { copy(screenState = current.copy(isDarkTheme = isDark)) }
            }
        } catch {
            // ignore — UI remains in previous state
        }
    }

    private fun handleLogout() {
        launchTry {
            setState { copy(screenState = SettingsState.ScreenState.Loading) }
            val result = logoutUseCase().toLogoutResult { it.asSnackBarMessage() }
            when (result) {
                is LogoutResult.Success -> setEvent { SettingsEvent.NavigateToLogin }
                is LogoutResult.Error -> {
                    setState { copy(screenState = SettingsState.ScreenState.Error) }
                    setEvent { SettingsEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(screenState = SettingsState.ScreenState.Error) }
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
