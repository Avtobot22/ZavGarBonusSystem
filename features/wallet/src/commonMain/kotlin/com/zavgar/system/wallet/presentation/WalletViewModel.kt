package com.zavgar.system.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.logout.LogoutHandler
import com.zavgar.system.domain.model.onTokenExpired
import com.zavgar.system.domain.usecase.GetSessionUseCase
import com.zavgar.system.domain.usecase.GetUserBalanceUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.info_offline_mode
import com.zavgar.system.wallet.mapper.toBalanceResult
import com.zavgar.system.wallet.mapper.toPresentation
import com.zavgar.system.wallet.model.BalanceResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletViewModel(
    private val getUserBalanceUseCase: GetUserBalanceUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val logoutHandler: LogoutHandler,
) : BaseViewModel<WalletState, WalletIntent, WalletEvent>(WalletState()) {

    private var cooldownJob: Job? = null

    companion object {
        private const val COOLDOWN_SECONDS = 10
    }

    init {
        initializeData()
    }

    override fun handleIntent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.RefreshBalance -> handleRefreshBalance()
            WalletIntent.Retry -> handleRetry()
            WalletIntent.PullToRefresh -> handlePullToRefresh()
        }
    }

    private fun handleRefreshBalance() {
        val screenState = currentState.screenState
        if (screenState !is WalletState.ScreenState.Content) return
        if (screenState.isRefreshing || screenState.timerSeconds > 0 || currentState.phone.isBlank()) return

        fetchBalance(isInitial = false)
    }

    private fun handleRetry() {
        setState { copy(screenState = WalletState.ScreenState.Loading) }
        initializeData()
    }

    private fun handlePullToRefresh() {
        val screenState = currentState.screenState
        if (screenState is WalletState.ScreenState.Content && screenState.isRefreshing) return
        fetchBalance(isInitial = true)
    }

    private fun fetchBalance(isInitial: Boolean) {
        viewModelScope.launch {
            if (isInitial) {
                setState { copy(screenState = WalletState.ScreenState.Loading) }
            } else {
                val current = currentState.screenState
                if (current is WalletState.ScreenState.Content) {
                    setState { copy(screenState = current.copy(isRefreshing = true)) }
                } else {
                    setState {
                        copy(screenState = WalletState.ScreenState.Content(isRefreshing = true))
                    }
                }
            }

            val appResult = getUserBalanceUseCase().onTokenExpired(logoutHandler) ?: return@launch
            when (val result = appResult.toBalanceResult()) {
                is BalanceResult.Success -> {
                    val current = currentState.screenState
                    val content = if (current is WalletState.ScreenState.Content) {
                        current.copy(balance = result.balance, isRefreshing = false)
                    } else {
                        WalletState.ScreenState.Content(balance = result.balance)
                    }
                    setState { copy(screenState = content) }
                    if (!isInitial) startCooldownTimer()
                }

                is BalanceResult.Error -> {
                    if (isInitial) {
                        if (currentState.phone.isNotBlank()) {
                            setState { copy(screenState = WalletState.ScreenState.Offline) }
                            setEvent {
                                WalletEvent.ShowSnackbar(
                                    SnackBarMessage(
                                        message = UiText.Resource(Res.string.info_offline_mode),
                                        type = SnackBarType.INFO
                                    )
                                )
                            }
                        } else {
                            setState { copy(screenState = WalletState.ScreenState.Error) }
                            setEvent { WalletEvent.ShowSnackbar(result.message) }
                        }
                    } else {
                        val current = currentState.screenState
                        if (current is WalletState.ScreenState.Content) {
                            setState { copy(screenState = current.copy(isRefreshing = false)) }
                        }
                        setEvent { WalletEvent.ShowSnackbar(result.message) }
                    }
                }
            }
        }
    }

    private fun startCooldownTimer() {
        cooldownJob?.cancel()

        val current = currentState.screenState
        if (current is WalletState.ScreenState.Content) {
            setState { copy(screenState = current.copy(timerSeconds = COOLDOWN_SECONDS)) }
        }

        cooldownJob = viewModelScope.launch {
            for (seconds in (COOLDOWN_SECONDS - 1) downTo 0) {
                delay(1000)
                val state = currentState.screenState
                if (state is WalletState.ScreenState.Content) {
                    setState { copy(screenState = state.copy(timerSeconds = seconds)) }
                }
            }
        }
    }

    private fun initializeData() {
        viewModelScope.launch {
            getSessionUseCase().toPresentation().fold(onSuccess = { session ->
                setState { copy(phone = session.phone) }
                fetchBalance(isInitial = true)
            }, onFailure = { logoutHandler.logout() })
        }
    }

    override fun onCleared() {
        super.onCleared()
        cooldownJob?.cancel()
    }
}