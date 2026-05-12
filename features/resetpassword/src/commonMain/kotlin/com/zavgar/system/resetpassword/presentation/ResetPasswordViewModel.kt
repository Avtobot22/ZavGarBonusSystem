package com.zavgar.system.resetpassword.presentation

import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.auth.usecase.ResetPasswordUseCase
import com.zavgar.system.utils.validation.ValidatePasswordUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import com.zavgar.system.utils.validation.ValidateRepeatedPasswordUseCase
import com.zavgar.system.resetpassword.mapper.asSnackBarMessage
import com.zavgar.system.resetpassword.mapper.toResetPasswordResult
import com.zavgar.system.domain.auth.model.ResetPasswordRequest
import com.zavgar.system.resetpassword.model.ResetPasswordResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.asUiText
import com.zavgar.system.utils.validation.toPresentation

class ResetPasswordViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatedPasswordUseCase: ValidateRepeatedPasswordUseCase
) : BaseViewModel<ResetPasswordState, ResetPasswordIntent, ResetPasswordEvent>(ResetPasswordState()) {

    override fun handleIntent(intent: ResetPasswordIntent) {
        when (intent) {
            is ResetPasswordIntent.EnterPhone -> handleEnterPhone(intent.phone)
            is ResetPasswordIntent.EnterPassword -> handleEnterPassword(intent.password)
            is ResetPasswordIntent.EnterRepeatPassword -> handleEnterRepeatPassword(intent.repeatPassword)
            is ResetPasswordIntent.Submit -> handleSubmit()
            is ResetPasswordIntent.ClickLogin -> handleClickLogin()
        }
    }

    companion object {
        private const val MAX_PHONE_LENGTH = 10
    }

    private fun handleEnterPhone(phone: String) {
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

    private fun handleEnterPassword(password: String) = setState {
        copy(
            password = password,
            passwordError = null
        )
    }

    private fun handleEnterRepeatPassword(repeatPassword: String) = setState {
        copy(
            repeatPassword = repeatPassword,
            repeatPasswordError = null
        )
    }

    private fun handleSubmit() {
        if (currentState.screenState is ResetPasswordState.ScreenState.Submitting) return

        val state = currentState

        val phoneResult = validatePhoneUseCase(state.phone).toPresentation { it.asUiText() }
        val passwordResult =
            validatePasswordUseCase(state.password).toPresentation { it.asUiText() }

        val repeatPasswordResult = validateRepeatedPasswordUseCase(
            state.password,
            state.repeatPassword
        ).toPresentation { it.asUiText() }

        val isFormValid = formValidation(
            phoneResult,
            passwordResult,
            repeatPasswordResult
        )

        if (!isFormValid) return

        performResetPassword(ResetPasswordRequest(state.phone, state.password))
    }

    private fun handleClickLogin() = setEvent { ResetPasswordEvent.NavigateToLogin }

    private fun performResetPassword(resetPasswordRequest: ResetPasswordRequest) {

        launchTry {
            setState { copy(screenState = ResetPasswordState.ScreenState.Submitting) }

            val result = resetPasswordUseCase(resetPasswordRequest).toResetPasswordResult { it.asSnackBarMessage() }

            when (result) {
                is ResetPasswordResult.Success -> setEvent { ResetPasswordEvent.NavigateToConfirm(resetPasswordRequest.phone) }
                is ResetPasswordResult.Error -> setEvent { ResetPasswordEvent.ShowSnackbar(result.message) }
            }

            setState { copy(screenState = ResetPasswordState.ScreenState.Idle) }
        } catch {
            setState { copy(screenState = ResetPasswordState.ScreenState.Idle) }
            setEvent {
                ResetPasswordEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }

    }

    private fun formValidation(
        phoneResult: ValidationResult<UiText>,
        passwordResult: ValidationResult<UiText>,
        repeatPasswordResult: ValidationResult<UiText>
    ): Boolean {

        val result = listOf(
            phoneResult,
            passwordResult,
            repeatPasswordResult
        ).any { it is ValidationResult.Error }

        setState {
            copy(
                phoneError = phoneResult.errorOrNull(),
                passwordError = passwordResult.errorOrNull(),
                repeatPasswordError = repeatPasswordResult.errorOrNull()
            )
        }

        return !result
    }

    private fun ValidationResult<UiText>.errorOrNull(): UiText? {
        return (this as? ValidationResult.Error)?.error
    }
}