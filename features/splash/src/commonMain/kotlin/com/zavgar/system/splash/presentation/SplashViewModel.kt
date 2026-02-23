package com.zavgar.system.splash.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.usecase.GetSessionUseCase
import com.zavgar.system.splash.mapper.toPresentation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel(
    private val getSessionUseCase: GetSessionUseCase
) : BaseViewModel<SplashState, SplashIntent, SplashEvent>(SplashState) {

    init {
        checkSession()
    }

    override fun handleIntent(intent: SplashIntent) {
        // пустая реализация нет интентов
    }

    // TODO Сейчас если потух токен рефреша то сразу после перехода, нас снова выкенет на логин, можно подумать о проверке рефреша здесь
    private fun checkSession() {
        viewModelScope.launch {
            val result = getSessionUseCase().toPresentation()

            delay(1000)

            result.fold(
                onSuccess = {
                    setEvent { SplashEvent.NavigateToWallet }
                },
                onFailure = {
                    setEvent { SplashEvent.NavigateToLogin }
                }
            )
        }
    }
}