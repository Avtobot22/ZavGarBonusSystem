package com.zavgar.system.registration.presentation

import com.zavgar.system.core.presentation.util.UiText
import kotlinx.datetime.LocalDate

data class RegisterState(

    val name: String = "",
    val birthDate: LocalDate? = null,
    val birthDateText: String = "",
    val phone: String = "",

    val privacyPolicyUrl: String = "",

    val nameError: UiText? = null,
    val birthDateError: UiText? = null,
    val phoneError: UiText? = null,

    val isPhoneValid: Boolean = false,

    val isDatePickerOpen: Boolean = false,

    val screenState: ScreenState = ScreenState.Idle
) {
    sealed interface ScreenState {
        data object Idle : ScreenState
        data object Submitting : ScreenState
    }

    val isFormFilled: Boolean
        get() = name.isNotBlank() &&
                birthDate != null &&
                phone.isNotBlank()

    val isRegisterButtonEnabled: Boolean
        get() = isFormFilled && screenState is ScreenState.Idle
}
