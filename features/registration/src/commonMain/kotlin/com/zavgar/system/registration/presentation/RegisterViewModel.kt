package com.zavgar.system.registration.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.usecase.RegisterUseCase
import com.zavgar.system.domain.usecase.validation.ValidateBirthDateUseCase
import com.zavgar.system.domain.usecase.validation.ValidateNameUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePasswordUseCase
import com.zavgar.system.domain.usecase.validation.ValidatePhoneUseCase
import com.zavgar.system.domain.usecase.validation.ValidateRepeatedPasswordUseCase
import com.zavgar.system.registration.mapper.asUiText
import com.zavgar.system.registration.mapper.toDomain
import com.zavgar.system.registration.mapper.toRegisterResult
import com.zavgar.system.registration.model.RegisterRequest
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.sharedValidation.ValidationResult
import com.zavgar.system.sharedValidation.asUiText
import com.zavgar.system.sharedValidation.toPresentation
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class RegisterViewModel(
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase,
    private val validateRepeatedPasswordUseCase: ValidateRepeatedPasswordUseCase,
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<RegisterState, RegisterIntent, RegisterEvent>(RegisterState()) {

    override fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EnterName -> handleNameInput(intent.name)
            is RegisterIntent.CloseDatePicker -> handleCloseDatePicker(intent.birthDate)
            is RegisterIntent.EnterPhone -> handlePhoneInput(intent.phone)
            is RegisterIntent.EnterPassword -> handlePasswordInput(intent.password)
            is RegisterIntent.EnterRepeatPassword -> handleRepeatPasswordInput(intent.repeatPassword)
            is RegisterIntent.OpenDatePicker -> handleOpenDatePicker()
            is RegisterIntent.DismissDatePicker -> handleDismissDatePicker()
            is RegisterIntent.ClickLogin -> handleClickLogin()
            is RegisterIntent.Submit -> handleSubmit()
        }
    }

    companion object {
        private const val MAX_PHONE_LENGTH = 10
    }

    private fun handleNameInput(name: String) =
        setState {
            copy(
                name = name,
                nameError = null
            )
        }

    private fun handleCloseDatePicker(birthDate: LocalDate) =
        setState {
            copy(
                birthDate = birthDate,
                birthDateError = null,
                birthDateText = birthDate.toDisplayString(),
                isDatePickerOpen = false
            )
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

    private fun handlePasswordInput(password: String) = setState {
        copy(
            password = password,
            passwordError = null
        )
    }

    private fun handleRepeatPasswordInput(repeatPassword: String) = setState {
        copy(
            repeatPassword = repeatPassword,
            repeatPasswordError = null
        )
    }

    private fun handleOpenDatePicker() = setState { copy(isDatePickerOpen = true) }

    private fun handleDismissDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleClickLogin() = setEvent { RegisterEvent.NavigateToLogin }

    private fun handleSubmit() {
        if (currentState.isLoading) return

        val state = currentState
        val nameResult = validateNameUseCase(state.name).toPresentation { it.asUiText() }
        val birthDateResult =
            validateBirthDateUseCase(state.birthDate).toPresentation { it.asUiText() }
        val phoneResult = validatePhoneUseCase(state.phone).toPresentation { it.asUiText() }
        val passwordResult =
            validatePasswordUseCase(state.password).toPresentation { it.asUiText() }

        val repeatPasswordResult = validateRepeatedPasswordUseCase(
            state.password,
            state.repeatPassword
        ).toPresentation { it.asUiText() }

        val isFormValid = formValidation(
            nameResult,
            birthDateResult,
            phoneResult,
            passwordResult,
            repeatPasswordResult
        )

        if (!isFormValid) return
        val birthDate = state.birthDate ?: return

        performRegister(
            RegisterRequest(
                phone = state.phone,
                name = state.name,
                birthDate = birthDate,
                password = state.password
            )
        )
    }

    private fun formValidation(
        nameResult: ValidationResult<UiText>,
        birthDateResult: ValidationResult<UiText>,
        phoneResult: ValidationResult<UiText>,
        passwordResult: ValidationResult<UiText>,
        repeatPasswordResult: ValidationResult<UiText>
    ): Boolean {
        val hasErrors = listOf(
            nameResult,
            birthDateResult,
            phoneResult,
            passwordResult,
            repeatPasswordResult
        ).any { it is ValidationResult.Error }

        setState {
            copy(
                nameError = nameResult.errorOrNull(),
                birthDateError = birthDateResult.errorOrNull(),
                phoneError = phoneResult.errorOrNull(),
                passwordError = passwordResult.errorOrNull(),
                repeatPasswordError = repeatPasswordResult.errorOrNull()
            )
        }

        return !hasErrors
    }

    private fun performRegister(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result =
                registerUseCase(registerRequest.toDomain()).toRegisterResult { it.asUiText() }

            setState { copy(isLoading = false) }

            when (result) {
                is RegisterResult.Success -> {
                    setEvent { RegisterEvent.NavigateToConfirm(registerRequest.phone) }
                }

                is RegisterResult.Error -> {
                    setEvent { RegisterEvent.ShowSnackbar(result.message) }
                }

            }
        }
    }

    private fun ValidationResult<UiText>.errorOrNull(): UiText? {
        return (this as? ValidationResult.Error)?.error
    }
}