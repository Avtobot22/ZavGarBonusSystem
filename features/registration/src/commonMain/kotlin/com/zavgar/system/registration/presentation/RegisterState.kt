package com.zavgar.system.registration.presentation

import com.zavgar.system.core.presentation.util.UiText
import kotlinx.datetime.LocalDate

data class RegisterState(

    val name: String = "",
    val birthDate: LocalDate? = null,
    val birthDateText: String = "",
    val phone: String = "",
    val password: String = "",
    val repeatPassword: String = "",

    val nameError: UiText? = null,
    val birthDateError: UiText? = null,
    val phoneError: UiText? = null,
    val passwordError: UiText? = null,
    val repeatPasswordError: UiText? = null,

    val isPhoneValid: Boolean = false,

    val isDatePickerOpen: Boolean = false,

    val isLoading: Boolean = false
)