package com.zavgar.system.resetpassword.presentation

sealed interface ResetPasswordIntent {

    data class EnterPhone(val phone: String) : ResetPasswordIntent

    data class EnterPassword(val password: String) : ResetPasswordIntent

    data class EnterRepeatPassword(val repeatPassword: String) : ResetPasswordIntent

    data object Submit : ResetPasswordIntent

    data object ClickLogin : ResetPasswordIntent
}