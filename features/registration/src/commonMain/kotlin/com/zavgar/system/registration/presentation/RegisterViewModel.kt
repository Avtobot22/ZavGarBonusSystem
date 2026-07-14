package com.zavgar.system.registration.presentation

import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.analytics.AuthFlow
import com.zavgar.system.config.AppConfig
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.asUiText
import com.zavgar.system.core.presentation.util.toDisplayString
import com.zavgar.system.domain.auth.model.RegisterRequest
import com.zavgar.system.domain.auth.usecase.RegisterUseCase
import com.zavgar.system.registration.mapper.asSnackBarMessage
import com.zavgar.system.registration.mapper.toRegisterResult
import com.zavgar.system.registration.model.RegisterResult
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.validation.ValidateBirthDateUseCase
import com.zavgar.system.utils.validation.ValidateNameUseCase
import com.zavgar.system.utils.validation.ValidatePhoneUseCase
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.sanitizePhone
import com.zavgar.system.utils.validation.toPresentation
import kotlinx.datetime.LocalDate

class RegisterViewModel(
    private val validateNameUseCase: ValidateNameUseCase,
    private val validateBirthDateUseCase: ValidateBirthDateUseCase,
    private val validatePhoneUseCase: ValidatePhoneUseCase,
    private val registerUseCase: RegisterUseCase,
    private val appConfig: AppConfig,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<RegisterState, RegisterIntent, RegisterEvent>(RegisterState()) {

    init {
        setState { copy(privacyPolicyUrl = appConfig.privacyPolicyUrl) }
    }

    override fun handleIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.EnterName -> handleNameInput(intent.name)
            is RegisterIntent.CloseDatePicker -> handleCloseDatePicker(intent.birthDate)
            is RegisterIntent.EnterPhone -> handlePhoneInput(intent.phone)
            is RegisterIntent.OpenDatePicker -> handleOpenDatePicker()
            is RegisterIntent.DismissDatePicker -> handleDismissDatePicker()
            is RegisterIntent.ClickLogin -> handleClickLogin()
            is RegisterIntent.Submit -> handleSubmit()
        }
    }

    private fun handleNameInput(name: String) =
        setState {
            copy(
                name = name,
                nameError = null,
            )
        }

    private fun handleCloseDatePicker(birthDate: LocalDate) =
        setState {
            copy(
                birthDate = birthDate,
                birthDateError = null,
                birthDateText = birthDate.toDisplayString(),
                isDatePickerOpen = false,
            )
        }

    private fun handlePhoneInput(phone: String) {
        val sanitizedPhone = sanitizePhone(phone)
        val validation = validatePhoneUseCase(sanitizedPhone).toPresentation { it.asUiText() }

        setState {
            copy(
                phone = sanitizedPhone,
                isPhoneValid = validation is ValidationResult.Valid,
                phoneError = null,
            )
        }
    }

    private fun handleOpenDatePicker() = setState { copy(isDatePickerOpen = true) }

    private fun handleDismissDatePicker() = setState { copy(isDatePickerOpen = false) }

    private fun handleClickLogin() {
        analyticsTracker.log(AnalyticsEvent.AuthLinkClick(AuthFlow.LOGIN))
        setEvent { RegisterEvent.NavigateBack }
    }

    private fun handleSubmit() {
        if (currentState.screenState is RegisterState.ScreenState.Submitting) return

        val state = currentState
        val nameResult = validateNameUseCase(state.name).toPresentation { it.asUiText() }
        val birthDateResult = validateBirthDateUseCase(state.birthDate).toPresentation { it.asUiText() }
        val phoneResult = validatePhoneUseCase(state.phone).toPresentation { it.asUiText() }

        val hasErrors = listOf(nameResult, birthDateResult, phoneResult).any { it is ValidationResult.Invalid }

        setState {
            copy(
                nameError = (nameResult as? ValidationResult.Invalid)?.error,
                birthDateError = (birthDateResult as? ValidationResult.Invalid)?.error,
                phoneError = (phoneResult as? ValidationResult.Invalid)?.error,
            )
        }

        if (hasErrors) return
        val birthDate = state.birthDate ?: return

        performRegister(RegisterRequest(phone = state.phone, name = state.name, birthDate = birthDate))
    }

    private fun performRegister(registerRequest: RegisterRequest) {
        launchTry {
            setState { copy(screenState = RegisterState.ScreenState.Submitting) }

            val result = registerUseCase(registerRequest).toRegisterResult { it.asSnackBarMessage() }

            setState { copy(screenState = RegisterState.ScreenState.Idle) }

            when (result) {
                is RegisterResult.Success -> {
                    analyticsTracker.log(AnalyticsEvent.OtpRequested(AuthFlow.REGISTRATION))
                    setEvent { RegisterEvent.NavigateToConfirm(registerRequest.phone) }
                }

                is RegisterResult.Error -> {
                    setEvent { RegisterEvent.ShowSnackbar(result.message) }
                }
            }
        } catch {
            setState { copy(screenState = RegisterState.ScreenState.Idle) }
            setEvent {
                RegisterEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }
}
