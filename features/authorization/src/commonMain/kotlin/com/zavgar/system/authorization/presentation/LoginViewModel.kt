package com.zavgar.system.authorization.presentation

import com.zavgar.system.authorization.mapper.asSnackBarMessage
import com.zavgar.system.authorization.mapper.toLoginResult
import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.authorization.domain.model.LoginRequest
import com.zavgar.system.authorization.domain.usecase.LoginUseCase
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.utils.validation.ValidatePasswordUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.asUiText
import com.zavgar.system.utils.validation.toPresentation

class LoginViewModel(
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginState, LoginIntent, LoginEvent>(LoginState()) {

    override fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EnterPhone -> handlePhoneInput(intent.phone)
            is LoginIntent.EnterPassword -> handlePasswordInput(intent.password)
            is LoginIntent.Submit -> handleSubmit()
            is LoginIntent.ClickForgotPassword -> handleForgotPassword()
            is LoginIntent.ClickRegister -> handleRegister()
        }
    }

    companion object {
        private const val MAX_PHONE_LENGTH = 10
    }

    private fun handlePhoneInput(phone: String) {
        val trimmedPhone = phone.take(MAX_PHONE_LENGTH).filter { it.isDigit() }
        val validation = validatePhoneUseCase(trimmedPhone).toPresentation { it.asUiText() }
        setState {
            copy(
                phone = trimmedPhone,
                isPhoneValid = validation is ValidationResult.Success,
                phoneError = null
            )
        }
    }

    private fun handlePasswordInput(password: String) =
        setState {
            copy(
                password = password,
                passwordError = null
            )
        }

    private fun handleSubmit() {
        if (currentState.screenState is LoginState.ScreenState.Submitting) return

        val state = currentState
        val phoneResult = validatePhoneUseCase(state.phone).toPresentation { it.asUiText() }
        val passwordResult =
            validatePasswordUseCase(state.password).toPresentation { it.asUiText() }

        val isPhoneValid = phoneResult is ValidationResult.Success
        val isPasswordValid = passwordResult is ValidationResult.Success

        setState {
            copy(
                isPhoneValid = isPhoneValid,
                phoneError = if (phoneResult is ValidationResult.Error) phoneResult.error else null,
                passwordError = if (passwordResult is ValidationResult.Error) passwordResult.error else null
            )
        }

        if (!isPhoneValid || !isPasswordValid) return

        performLogin(LoginRequest(phone = state.phone, password = state.password))
    }

    private fun handleForgotPassword() = setEvent { LoginEvent.NavigateToForgotPassword }

    private fun handleRegister() = setEvent { LoginEvent.NavigateToRegister }

    private fun performLogin(loginRequest: LoginRequest) {
        launchTry {
            setState { copy(screenState = LoginState.ScreenState.Submitting) }

            val result = loginUseCase(loginRequest).toLoginResult { it.asSnackBarMessage() }

            when (result) {
                is LoginResult.Success -> setEvent { LoginEvent.NavigateToWallet }
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
