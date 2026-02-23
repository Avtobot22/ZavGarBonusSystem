package com.zavgar.system.domain.model.validation

sealed interface PasswordValidationError {
    data object Blank : PasswordValidationError

    data object Short : PasswordValidationError
}