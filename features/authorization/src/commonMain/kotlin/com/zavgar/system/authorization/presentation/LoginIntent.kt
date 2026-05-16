package com.zavgar.system.authorization.presentation

sealed interface LoginIntent {

    data class EnterPhone(val phone: String) : LoginIntent

    data object Submit : LoginIntent

    data object ClickRegister : LoginIntent
}
