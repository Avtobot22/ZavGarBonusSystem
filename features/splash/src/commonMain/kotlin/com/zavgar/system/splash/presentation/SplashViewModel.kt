package com.zavgar.system.splash.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.datastore.datasource.OnboardingDataSource
import com.zavgar.system.domain.session.usecase.GetSessionUseCase
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.flow.first


class SplashViewModel(
    private val remoteConfigService: RemoteConfigService,
    private val getSessionUseCase: GetSessionUseCase,
    private val onboardingDataSource: OnboardingDataSource,
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    init {
        checkSession()
    }

    override fun handleIntent(intent: SplashIntent) = Unit

    private fun checkSession() {
        launchTry {
            remoteConfigService.activate()

            val result = getSessionUseCase()
            val isOnboardingCompleted = onboardingDataSource.isOnboardingCompleted.first()

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
