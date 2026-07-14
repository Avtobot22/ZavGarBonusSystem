package com.zavgar.system.settings.presentation

import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.loading.ScreenLoadExecutionResult
import com.zavgar.system.core.presentation.loading.ScreenLoadPolicy
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.theme.usecase.ObserveDarkThemeUseCase
import com.zavgar.system.domain.theme.usecase.SetDarkThemeUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserProfileUseCase
import com.zavgar.system.domain.userinfo.usecase.LogoutUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.settings.mapper.asSnackBarMessage
import com.zavgar.system.settings.mapper.toLogoutResult
import com.zavgar.system.settings.model.LogoutResult
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first

class SettingsViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getUserBalanceUseCase: GetUserBalanceUseCase,
    private val observeDarkThemeUseCase: ObserveDarkThemeUseCase,
    private val setDarkThemeUseCase: SetDarkThemeUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<SettingsState, SettingsIntent, SettingsEvent>(SettingsState()) {

    private var loadJob: Job? = null
    private val loadPolicy = ScreenLoadPolicy()

    override fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.ScreenEntered -> loadData(force = false)
            is SettingsIntent.ToProfileDetail -> handleToProfileDetail()
            is SettingsIntent.Logout -> handleLogout()
            is SettingsIntent.Retry -> retryData()
            is SettingsIntent.ToggleDarkMode -> handleToggleDarkMode(intent.isDark)
        }
    }

    private fun retryData() {
        loadData(force = true)
    }

    private fun loadData(force: Boolean) {
        val canStart = if (force) {
            loadPolicy.canStartForcedLoad(loadJob)
        } else {
            loadPolicy.canStartAutomaticLoad(loadJob)
        }
        if (!canStart) return

        loadJob = launchTry {
            val hadContent = currentState.profileState is SettingsState.ProfileState.Content
            if (!hadContent) setState { copy(profileState = SettingsState.ProfileState.Loading) }

            when (
                val execution = loadPolicy.executeWithTimeout {
                    coroutineScope {
                        val profileDeferred = async { getUserProfileUseCase() }
                        val balanceDeferred = async { getUserBalanceUseCase() }
                        Triple(
                            profileDeferred.await(),
                            balanceDeferred.await(),
                            observeDarkThemeUseCase().first(),
                        )
                    }
                }
            ) {
                is ScreenLoadExecutionResult.Completed -> {
                    val (profileResult, balanceResult, isDarkTheme) = execution.value
                    setState { copy(isDarkTheme = isDarkTheme) }
                    if (profileResult is AppResult.Success && balanceResult is AppResult.Success) {
                        loadPolicy.markSuccessfulLoad()
                        setState {
                            copy(
                                profileState = SettingsState.ProfileState.Content(
                                    name = profileResult.data.name,
                                    phone = profileResult.data.phone,
                                    balance = balanceResult.data.balance,
                                ),
                            )
                        }
                    } else if (!hadContent) {
                        setState { copy(profileState = SettingsState.ProfileState.Error) }
                    }
                }

                ScreenLoadExecutionResult.TimedOut -> handleLoadFailure(hadContent)
            }
        } catch {
            handleLoadFailure(currentState.profileState is SettingsState.ProfileState.Content)
        }
    }

    private fun handleLoadFailure(hadContent: Boolean) {
        if (!hadContent) setState { copy(profileState = SettingsState.ProfileState.Error) }
        setEvent {
            SettingsEvent.ShowSnackbar(
                SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
            )
        }
    }

    private fun handleToggleDarkMode(isDark: Boolean) {
        launchTry {
            setDarkThemeUseCase(isDark)
            setState { copy(isDarkTheme = isDark) }
        } catch {
        }
    }

    private fun handleLogout() {
        if (currentState.isLoggingOut) return
        launchTry {
            setState { copy(isLoggingOut = true) }
            val result = logoutUseCase().toLogoutResult { it.asSnackBarMessage() }
            when (result) {
                is LogoutResult.Success -> {
                    analyticsTracker.log(AnalyticsEvent.Logout)
                    analyticsTracker.clearUser()
                    setEvent { SettingsEvent.NavigateToLogin }
                }
                is LogoutResult.Error -> {
                    setState { copy(isLoggingOut = false) }
                    setEvent { SettingsEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(isLoggingOut = false) }
            setEvent {
                SettingsEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }

    private fun handleToProfileDetail() {
        setEvent { SettingsEvent.NavigateToProfileDetail }
    }
}
