package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.PasswordValidationError
import com.zavgar.system.domain.model.validation.ValidationResult

class ValidatePasswordUseCase {
    operator fun invoke(password: String): ValidationResult<PasswordValidationError> {
        if (password.isBlank()) {
            return ValidationResult.Error(PasswordValidationError.Blank)
        }

        if (password.length < 8) {
            return ValidationResult.Error(PasswordValidationError.Short)
        }

        return ValidationResult.Success

    }
}