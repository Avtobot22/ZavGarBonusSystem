package com.zavgar.system.domain.model.validation

sealed interface NameValidationError {
    data object Blank : NameValidationError
}