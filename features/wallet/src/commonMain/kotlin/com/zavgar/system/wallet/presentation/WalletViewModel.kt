package com.zavgar.system.wallet.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.usecase.DeleteSessionUseCase
import com.zavgar.system.domain.usecase.GetSessionUseCase
import com.zavgar.system.domain.usecase.GetUserBalanceUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.wallet.mapper.toBalanceResult
import com.zavgar.system.wallet.mapper.toPresentation
import com.zavgar.system.wallet.model.BalanceResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalletViewModel(
    private val getUserBalanceUseCase: GetUserBalanceUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : BaseViewModel<WalletState, WalletIntent, WalletEvent>(WalletState()) {

    private var cooldownJob: Job? = null

    private val COOLDOWN_SECONDS = 10

    init {
        initializeData()
    }

    override fun handleIntent(intent: WalletIntent) {
        when (intent) {
            WalletIntent.RefreshBalance -> handleRefreshBalance()
        }
    }

    private fun handleRefreshBalance() {
        val state = currentState

        if (state.isRefreshing || state.timerSeconds > 0 || state.phone.isBlank()) return

        fetchBalance(isInitial = false)
    }

    private fun fetchBalance(isInitial: Boolean) {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            val result = getUserBalanceUseCase().toBalanceResult()

            setState { copy(isRefreshing = false) }

            when (result) {
                is BalanceResult.Success -> {
                    setState {
                        copy(
                            screenState = WalletState.ScreenState.Content,
                            balance = result.balance,
                        )
                    }
                    if (!isInitial) startCooldownTimer()
                }

                is BalanceResult.Error -> {
                    setState {
                        copy(
                            screenState = WalletState.ScreenState.Content,
                        )
                    }
                }

                is BalanceResult.TokenExpired -> handleLogout()
            }
        }
    }

    private fun startCooldownTimer() {
        cooldownJob?.cancel()

        setState { copy(timerSeconds = COOLDOWN_SECONDS) }

        cooldownJob = viewModelScope.launch {
            for (seconds in (COOLDOWN_SECONDS - 1) downTo 0) {
                delay(1000)
                setState { copy(timerSeconds = seconds) }
            }
        }
    }

    private fun initializeData() {
        viewModelScope.launch {
            getSessionUseCase().toPresentation().fold(
                onSuccess = { session ->
                    setState { copy(phone = session.phone) }
                    fetchBalance(isInitial = true)
                },
                onFailure = { handleLogout() }
            )
        }
    }

    private fun handleLogout() {
        viewModelScope.launch {
            val result = deleteSessionUseCase()
            result.getOrElse { exception ->
                setEvent {
                    WalletEvent.ShowSnackbar(exception.message?.let {
                        UiText.DynamicString(it)
                    } ?: UiText.Resource(Res.string.error_unknown_error))
                }
            }
        }
        setEvent { WalletEvent.NavigateToLogin }
    }
}