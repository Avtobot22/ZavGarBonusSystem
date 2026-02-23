package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.NameValidationError
import com.zavgar.system.domain.model.validation.ValidationResult

class ValidateNameUseCase {
    operator fun invoke(name: String): ValidationResult<NameValidationError> {
        if (name.isBlank()) {
            return ValidationResult.Error(NameValidationError.Blank)
        }

        return ValidationResult.Success
    }
}