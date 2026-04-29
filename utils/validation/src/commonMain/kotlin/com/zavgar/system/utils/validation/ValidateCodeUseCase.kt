package com.zavgar.system.utils.validation


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