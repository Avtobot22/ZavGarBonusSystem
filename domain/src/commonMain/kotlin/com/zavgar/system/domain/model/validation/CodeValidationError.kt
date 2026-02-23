package com.zavgar.system.domain.model.validation

sealed interface CodeValidationError {
    data object Blank : CodeValidationError
    data object TooShort : CodeValidationError
}