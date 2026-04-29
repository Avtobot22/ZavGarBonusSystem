package com.zavgar.system.utils.validation

sealed interface NameValidationError {
    data object Blank : NameValidationError
}