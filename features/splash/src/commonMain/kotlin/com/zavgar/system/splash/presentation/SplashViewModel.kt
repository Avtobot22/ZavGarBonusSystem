package com.zavgar.system.splash.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
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

            result.fold(
                onSuccess = { setEvent { SplashEvent.NavigateToWallet } },
                onFailure = { setEvent { SplashEvent.NavigateToLogin } }
            )
        } catch {
            setEvent { SplashEvent.NavigateToLogin }
        }
    }
}
