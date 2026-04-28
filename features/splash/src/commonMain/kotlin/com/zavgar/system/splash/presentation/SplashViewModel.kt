package com.zavgar.system.splash.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.usecase.GetSessionUseCase
import com.zavgar.system.splash.mapper.toPresentation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SPLASH_MIN_DELAY_MS = 1_000L

class SplashViewModel(
    private val getSessionUseCase: GetSessionUseCase,
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    init {
        checkSession()
    }

    override fun handleIntent(intent: SplashIntent) = Unit

    private fun checkSession() {
        viewModelScope.launch {
            val result = getSessionUseCase().toPresentation()

            delay(SPLASH_MIN_DELAY_MS)

            result.fold(
                onSuccess = { setEvent { SplashEvent.NavigateToWallet } },
                onFailure = { setEvent { SplashEvent.NavigateToLogin } }
            )
        }
    }
}
