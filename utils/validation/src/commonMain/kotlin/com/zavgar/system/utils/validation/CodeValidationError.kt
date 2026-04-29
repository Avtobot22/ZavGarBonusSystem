package com.zavgar.system.utils.validation

sealed interface CodeValidationError {
    data object Blank : CodeValidationError
    data object TooShort : CodeValidationError
}