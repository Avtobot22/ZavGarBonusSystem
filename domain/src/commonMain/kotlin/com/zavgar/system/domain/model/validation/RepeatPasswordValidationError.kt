package com.zavgar.system.domain.model.validation

sealed interface RepeatPasswordValidationError {
    data object NotMatching : RepeatPasswordValidationError
}