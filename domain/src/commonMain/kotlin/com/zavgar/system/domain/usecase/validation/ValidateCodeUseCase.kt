package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.CodeValidationError
import com.zavgar.system.domain.model.validation.ValidationResult

class ValidateCodeUseCase() {
    operator fun invoke(code: String): ValidationResult<CodeValidationError> {
        if (code.isBlank()) {
            return ValidationResult.Error(CodeValidationError.Blank)
        }
        if (code.length < 6) {
            return ValidationResult.Error(CodeValidationError.TooShort)
        }
        return ValidationResult.Success

    }
}