package com.zavgar.system.wallet.presentation

import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.loading.ScreenLoadExecutionResult
import com.zavgar.system.core.presentation.loading.ScreenLoadPolicy
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.session.LogoutHandler
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.domain.userinfo.usecase.GetCachedBalanceUseCase
import com.zavgar.system.domain.userinfo.usecase.GetMonthlyAccrualsUseCase
import com.zavgar.system.domain.userinfo.usecase.GetUserBalanceUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.resources.info_offline_mode
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.wallet.mapper.toBalanceResult
import com.zavgar.system.wallet.model.BalanceResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds

class WalletViewModel(
    private val getUserBalanceUseCase: GetUserBalanceUseCase,
    private val getCachedBalanceUseCase: GetCachedBalanceUseCase,
    private val getMonthlyAccrualsUseCase: GetMonthlyAccrualsUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val logoutHandler: LogoutHandler,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<WalletState, WalletIntent, WalletEvent>(WalletState()) {

    private var fetchJob: Job? = null
    private var monthlyAccrualsJob: Job? = null
    private var cooldownJob: Job? = null
    private var initJob: Job? = null
    private val loadPolicy = ScreenLoadPolicy()

    companion object {
        private const val COOLDOWN_SECONDS = 10
        private const val SECOND_MILLIS = 1000L
    }

    override fun handleIntent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.ScreenEntered -> handleScreenEntered()
            WalletIntent.RefreshBalance -> handleRefreshBalance()
            WalletIntent.Retry -> handleRetry()
            WalletIntent.PullToRefresh -> handlePullToRefresh()
        }
    }

    private fun handleScreenEntered() {
        if (!loadPolicy.canStartAutomaticLoad(activeLoadJob())) return
        initializeData(force = false)
    }

    private fun handleRefreshBalance() {
        val screenState = currentState.screenState
        if (screenState !is WalletState.ScreenState.Content) return
        if (screenState.isRefreshing || screenState.timerSeconds > 0 || currentState.phone.isBlank()) return

        fetchBalance(isInitial = false, force = true)
    }

    private fun handleRetry() {
        if (!loadPolicy.canStartForcedLoad(activeLoadJob())) return
        setState { copy(screenState = WalletState.ScreenState.Loading) }
        initializeData(force = true)
    }

    private fun handlePullToRefresh() {
        val screenState = currentState.screenState
        if (screenState is WalletState.ScreenState.Content && screenState.isRefreshing) return
        fetchBalance(isInitial = true, force = true)
    }

    private fun fetchBalance(isInitial: Boolean, force: Boolean) {
        val canStart = if (force) {
            loadPolicy.canStartForcedLoad(fetchJob)
        } else {
            loadPolicy.canStartAutomaticLoad(fetchJob)
        }
        if (!canStart) return

        fetchJob = launchTry {
            applyFetchStartState(isInitial)
            when (val execution = loadPolicy.executeWithTimeout { getUserBalanceUseCase() }) {
                is ScreenLoadExecutionResult.Completed -> {
                    when (val result = execution.value.toBalanceResult()) {
                        is BalanceResult.Success -> handleBalanceSuccess(result, isInitial)
                        is BalanceResult.Error -> handleBalanceError(result, isInitial)
                    }
                }

                ScreenLoadExecutionResult.TimedOut -> handleBalanceException(isInitial)
            }
        } catch {
            handleBalanceException(isInitial)
        }
    }

    private fun applyFetchStartState(isInitial: Boolean) {
        val current = currentState.screenState
        if (isInitial) {
            if (current is WalletState.ScreenState.Content) {
                setState { copy(screenState = current.copy(isRefreshing = true, isStale = false)) }
            } else {
                setState { copy(screenState = WalletState.ScreenState.Loading) }
            }
            return
        }
        if (current is WalletState.ScreenState.Content) {
            setState { copy(screenState = current.copy(isRefreshing = true, isStale = false)) }
        } else {
            setState { copy(screenState = WalletState.ScreenState.Content(isRefreshing = true)) }
        }
    }

    private fun handleBalanceSuccess(result: BalanceResult.Success, isInitial: Boolean) {
        loadPolicy.markSuccessfulLoad()
        val updatedAt = Clock.System.now().toEpochMilliseconds()
        val current = currentState.screenState
        val content = if (current is WalletState.ScreenState.Content) {
            current.copy(
                balance = result.balance,
                isRefreshing = false,
                isStale = false,
                lastUpdatedMillis = updatedAt,
            )
        } else {
            WalletState.ScreenState.Content(balance = result.balance, lastUpdatedMillis = updatedAt)
        }
        setState { copy(screenState = content) }
        analyticsTracker.log(AnalyticsEvent.WalletBalanceViewed)
        if (!isInitial) startCooldownTimer()
    }

    private fun handleBalanceError(result: BalanceResult.Error, isInitial: Boolean) {
        val current = currentState.screenState
        // Есть кэшированный контент — остаёмся на нём и показываем баннер «устаревшие данные»
        if (current is WalletState.ScreenState.Content) {
            setState { copy(screenState = current.copy(isRefreshing = false, isStale = true)) }
            setEvent { WalletEvent.ShowSnackbar(result.message) }
            return
        }
        if (!isInitial) {
            setEvent { WalletEvent.ShowSnackbar(result.message) }
            return
        }
        if (currentState.phone.isNotBlank()) {
            setState { copy(screenState = WalletState.ScreenState.Offline) }
            analyticsTracker.log(AnalyticsEvent.WalletBalanceError(errorType = "offline"))
            setEvent {
                WalletEvent.ShowSnackbar(
                    SnackBarMessage(
                        message = UiText.Resource(Res.string.info_offline_mode),
                        type = SnackBarType.INFO,
                    ),
                )
            }
        } else {
            setState { copy(screenState = WalletState.ScreenState.Error) }
            analyticsTracker.log(AnalyticsEvent.WalletBalanceError(errorType = "server"))
            setEvent { WalletEvent.ShowSnackbar(result.message) }
        }
    }

    private fun handleBalanceException(isInitial: Boolean) {
        val current = currentState.screenState
        if (current is WalletState.ScreenState.Content) {
            setState { copy(screenState = current.copy(isRefreshing = false, isStale = true)) }
        } else if (isInitial) {
            setState { copy(screenState = WalletState.ScreenState.Error) }
        }
        setEvent {
            WalletEvent.ShowSnackbar(
                SnackBarMessage(
                    message = UiText.Resource(Res.string.error_unknown_error),
                    type = SnackBarType.ERROR,
                ),
            )
        }
    }

    private fun startCooldownTimer() {
        cooldownJob?.cancel()

        val current = currentState.screenState
        if (current is WalletState.ScreenState.Content) {
            setState { copy(screenState = current.copy(timerSeconds = COOLDOWN_SECONDS)) }
        }

        cooldownJob = launchTry {
            for (seconds in (COOLDOWN_SECONDS - 1) downTo 0) {
                delay(SECOND_MILLIS.milliseconds)
                val state = currentState.screenState
                if (state is WalletState.ScreenState.Content) {
                    setState { copy(screenState = state.copy(timerSeconds = seconds)) }
                }
            }
        } catch {
        }
    }

    private fun initializeData(force: Boolean) {
        if (initJob?.isActive == true) return
        initJob = launchTry {
            when (
                val execution = loadPolicy.executeWithTimeout {
                    val session = getSessionUseCase()
                    val cachedBalance = if (session is AppResult.Success) {
                        getCachedBalanceUseCase()
                    } else {
                        null
                    }
                    session to cachedBalance
                }
            ) {
                is ScreenLoadExecutionResult.Completed -> {
                    val (result, cached) = execution.value
                    when (result) {
                        is AppResult.Success -> {
                            setState { copy(phone = result.data.phone) }
                            if (cached != null) {
                                setState {
                                    copy(
                                        screenState = WalletState.ScreenState.Content(
                                            balance = cached.balance,
                                            isRefreshing = true,
                                            lastUpdatedMillis = cached.updatedAtMillis,
                                        ),
                                    )
                                }
                            }
                            fetchBalance(isInitial = true, force = force)
                            fetchMonthlyAccruals()
                        }

                        is AppResult.Error -> logoutHandler.logout()
                    }
                }

                ScreenLoadExecutionResult.TimedOut -> handleBalanceException(isInitial = true)
            }
        } catch {
            handleBalanceException(isInitial = true)
        }
    }

    private fun fetchMonthlyAccruals() {
        if (monthlyAccrualsJob?.isActive == true) return

        monthlyAccrualsJob = launchTry {
            when (val execution = loadPolicy.executeWithTimeout { getMonthlyAccrualsUseCase() }) {
                is ScreenLoadExecutionResult.Completed -> when (val result = execution.value) {
                    is AppResult.Success -> setState { copy(monthlyEarned = result.data) }
                    is AppResult.Error -> Unit
                }

                ScreenLoadExecutionResult.TimedOut -> Unit
            }
        } catch {
        }
    }

    private fun activeLoadJob(): Job? =
        listOf(initJob, fetchJob, monthlyAccrualsJob).firstOrNull { it?.isActive == true }

    override fun onCleared() {
        super.onCleared()
        fetchJob?.cancel()
        monthlyAccrualsJob?.cancel()
        cooldownJob?.cancel()
        initJob?.cancel()
    }
}
