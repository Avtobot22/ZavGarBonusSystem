package com.zavgar.system.splash.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.onboarding.usecase.ObserveOnboardingCompletedUseCase
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.flow.first


class SplashViewModel(
    private val remoteConfigService: RemoteConfigService,
    private val getSessionUseCase: GetSessionUseCase,
    private val observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    init {
        checkSession()
    }

    override fun handleIntent(intent: SplashIntent) = Unit

    private fun checkSession() {
        launchTry {
            remoteConfigService.activate()

            val result = getSessionUseCase()
            val isOnboardingCompleted = observeOnboardingCompletedUseCase().first()

            when (result) {
                is AppResult.Success -> setEvent { SplashEvent.NavigateToWallet }
                is AppResult.Error -> setEvent {
                    if (isOnboardingCompleted) SplashEvent.NavigateToLogin
                    else SplashEvent.NavigateToOnboarding
                }
            }
        } catch {
            setEvent { SplashEvent.NavigateToLogin }
        }
    }
}
