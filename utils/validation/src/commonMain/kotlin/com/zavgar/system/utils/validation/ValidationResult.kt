package com.zavgar.system.utils.validation

sealed interface ValidationResult<out E> {
    data object Success : ValidationResult<Nothing>
    data class Error<E>(val error: E) : ValidationResult<E>
}
