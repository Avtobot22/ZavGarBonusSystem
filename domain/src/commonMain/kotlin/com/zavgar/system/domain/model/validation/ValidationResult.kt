package com.zavgar.system.domain.model.validation

sealed interface ValidationResult<out E> {
    data object Success : ValidationResult<Nothing>
    data class Error<E>(val error: E) : ValidationResult<E>
}