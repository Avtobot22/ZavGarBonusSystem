package com.zavgar.system.authorization.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.authorization.mapper.asSnackBarMessage
import com.zavgar.system.authorization.mapper.toDomain
import com.zavgar.system.authorization.mapper.toLoginResult
import com.zavgar.system.authorization.model.LoginRequest
import com.zavgar.system.authorization.model.LoginResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.domain.usecase.LoginUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePasswordUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePhoneUseCase
import com.zavgar.system.sharedValidation.ValidationResult
import com.zavgar.system.sharedValidation.asUiText
import com.zavgar.system.sharedValidation.toPresentation
import kotlinx.coroutines.launch

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
        if (currentState.isLoading) return

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
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = loginUseCase(loginRequest.toDomain()).toLoginResult { it.asSnackBarMessage() }

            when (result) {
                is LoginResult.Success -> {
                    setEvent { LoginEvent.NavigateToWallet }
                }

                is LoginResult.Error -> {
                    setEvent { LoginEvent.ShowSnackbar(result.message) }
                }
            }

            setState { copy(isLoading = false) }
        }
    }
}
