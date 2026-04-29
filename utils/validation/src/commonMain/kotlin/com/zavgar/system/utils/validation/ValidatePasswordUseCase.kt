package com.zavgar.system.utils.validation


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