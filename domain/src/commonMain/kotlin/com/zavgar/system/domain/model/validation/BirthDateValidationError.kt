package com.zavgar.system.domain.model.validation

sealed interface BirthDateValidationError {
    object Blank : BirthDateValidationError
}