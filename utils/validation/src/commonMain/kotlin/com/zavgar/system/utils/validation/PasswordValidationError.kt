package com.zavgar.system.utils.validation

sealed interface PasswordValidationError {
    data object Blank : PasswordValidationError

    data object Short : PasswordValidationError
}