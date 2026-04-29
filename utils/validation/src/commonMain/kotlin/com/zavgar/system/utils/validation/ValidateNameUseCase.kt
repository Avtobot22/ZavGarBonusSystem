package com.zavgar.system.utils.validation


class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult<NameValidationError> {
        if (name.isBlank()) {
            return ValidationResult.Error(NameValidationError.Blank)
        }

        return ValidationResult.Success
    }
}