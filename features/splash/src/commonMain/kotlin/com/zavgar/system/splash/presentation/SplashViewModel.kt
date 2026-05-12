package com.zavgar.system.splash.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.delay

private const val SPLASH_MIN_DELAY_MS = 1_000L

class SplashViewModel(
    private val getSessionUseCase: GetSessionUseCase,
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    init {
        checkSession()
    }

    override fun handleIntent(intent: SplashIntent) = Unit

    private fun checkSession() {
        launchTry {
            val result = getSessionUseCase()

            delay(SPLASH_MIN_DELAY_MS)

            when (result) {
                is AppResult.Success -> setEvent { SplashEvent.NavigateToWallet }
                is AppResult.Error -> setEvent { SplashEvent.NavigateToLogin }
            }
        } catch {
            setEvent { SplashEvent.NavigateToLogin }
        }
    }
}
