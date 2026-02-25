package com.zavgar.system.registration.presentation

import kotlinx.datetime.LocalDate

sealed interface RegisterIntent {

    data class EnterName(val name: String) : RegisterIntent

    data class CloseDatePicker(val birthDate: LocalDate) : RegisterIntent

    data class EnterPhone(val phone: String) : RegisterIntent

    data class EnterPassword(val password: String) : RegisterIntent

    data class EnterRepeatPassword(val repeatPassword: String) : RegisterIntent

    data object Submit : RegisterIntent

    data object ClickLogin : RegisterIntent

    data object OpenDatePicker : RegisterIntent

    data object DismissDatePicker : RegisterIntent
}