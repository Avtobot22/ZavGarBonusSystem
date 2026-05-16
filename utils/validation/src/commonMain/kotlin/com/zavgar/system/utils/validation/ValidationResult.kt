package com.zavgar.system.utils.validation

sealed interface ValidationResult<out E> {
    data object Valid : ValidationResult<Nothing>
    data class Invalid<E>(val error: E) : ValidationResult<E>
}
