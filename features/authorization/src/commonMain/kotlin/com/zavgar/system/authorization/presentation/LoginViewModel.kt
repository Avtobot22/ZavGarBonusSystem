package com.zavgar.system.authorization.presentation

import com.zavgar.system.authorization.mapper.asSnackBarMessage
import com.zavgar.system.authorization.mapper.toLoginResult
import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.auth.model.LoginRequest
import com.zavgar.system.domain.auth.usecase.LoginUseCase
import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.analytics.AuthFlow
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.asUiText
import com.zavgar.system.utils.validation.sanitizePhone
import com.zavgar.system.utils.validation.toPresentation

class LoginViewModel(
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val loginUseCase: LoginUseCase,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<LoginState, LoginIntent, LoginEvent>(LoginState()) {

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EnterPhone -> handlePhoneInput(intent.phone)
            is LoginIntent.Submit -> handleSubmit()
            is LoginIntent.ClickRegister -> handleRegister()
        }
    }

    private fun handlePhoneInput(phone: String) {
        val sanitizedPhone = sanitizePhone(phone)
        val validation = validatePhoneUseCase(sanitizedPhone).toPresentation { it.asUiText() }
        setState {
            copy(
                phone = sanitizedPhone,
                isPhoneValid = validation is ValidationResult.Valid,
                phoneError = null
            )
        }
    }

    private fun handleSubmit() {
        if (currentState.screenState is LoginState.ScreenState.Submitting) return

        val state = currentState
        val phoneResult = validatePhoneUseCase(state.phone).toPresentation { it.asUiText() }
        val isPhoneValid = phoneResult is ValidationResult.Valid

        setState {
            copy(
                isPhoneValid = isPhoneValid,
                phoneError = if (phoneResult is ValidationResult.Invalid) phoneResult.error else null,
            )
        }

        if (!isPhoneValid) return

        requestOtp(LoginRequest(phone = state.phone))
    }

    private fun handleRegister() {
        analyticsTracker.log(AnalyticsEvent.AuthLinkClick(AuthFlow.REGISTRATION))
        setEvent { LoginEvent.NavigateToRegister }
    }

    private fun requestOtp(loginRequest: LoginRequest) {
        launchTry {
            setState { copy(screenState = LoginState.ScreenState.Submitting) }

            val result = loginUseCase(loginRequest).toLoginResult { it.asSnackBarMessage() }

            when (result) {
                is LoginResult.Success -> {
                    analyticsTracker.log(AnalyticsEvent.OtpRequested(AuthFlow.LOGIN))
                    setEvent { LoginEvent.NavigateToConfirmation(loginRequest.phone) }
                }
                is LoginResult.Error -> setEvent { LoginEvent.ShowSnackbar(result.message) }
            }

            setState { copy(screenState = LoginState.ScreenState.Idle) }
        } catch {
            setState { copy(screenState = LoginState.ScreenState.Idle) }
            setEvent {
                LoginEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }
}
