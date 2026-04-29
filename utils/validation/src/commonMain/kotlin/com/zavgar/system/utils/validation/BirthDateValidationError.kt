package com.zavgar.system.utils.validation

sealed interface BirthDateValidationError {
    object Blank : BirthDateValidationError
}