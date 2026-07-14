package com.zavgar.system.splash.presentation

import com.zavgar.system.config.AppConfig
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.onboarding.usecase.ObserveOnboardingCompletedUseCase
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first

class SplashViewModel(
    private val appConfig: AppConfig,
    private val getSessionUseCase: GetSessionUseCase,
    private val observeOnboardingCompletedUseCase: ObserveOnboardingCompletedUseCase,
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    private var checkSessionJob: Job? = null
    private var unauthenticatedRouteJob: Job? = null

    override fun handleIntent(intent: SplashIntent) {
        when (intent) {
            SplashIntent.ScreenEntered -> checkSession()
        }
    }

    private fun checkSession() {
        if (checkSessionJob?.isActive == true ||
            unauthenticatedRouteJob?.isActive == true
        ) {
            return
        }

        checkSessionJob = launchTry {
            appConfig.activate()

            when (getSessionUseCase()) {
                is AppResult.Success -> setEvent { SplashEvent.NavigateToWallet }
                is AppResult.Error -> routeUnauthenticated()
            }
        } catch {
            routeUnauthenticated()
        }
    }

    private fun routeUnauthenticated() {
        if (unauthenticatedRouteJob?.isActive == true) return

        unauthenticatedRouteJob = launchTry {
            val isOnboardingCompleted = observeOnboardingCompletedUseCase().first()
            setEvent {
                if (isOnboardingCompleted) SplashEvent.NavigateToLogin else SplashEvent.NavigateToOnboarding
            }
        } catch {
            setEvent { SplashEvent.NavigateToLogin }
        }
    }
}
