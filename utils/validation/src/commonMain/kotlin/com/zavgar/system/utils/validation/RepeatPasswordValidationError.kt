package com.zavgar.system.utils.validation

sealed interface RepeatPasswordValidationError {
    data object NotMatching : RepeatPasswordValidationError
}