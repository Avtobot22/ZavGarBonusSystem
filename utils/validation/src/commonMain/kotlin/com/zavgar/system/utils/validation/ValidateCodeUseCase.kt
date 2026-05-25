package com.zavgar.system.utils.validation

class ValidateCodeUseCase {

    operator fun invoke(code: String): ValidationResult<CodeValidationError> =
        validate {
            notBlank(code, CodeValidationError.BLANK)
            minLength(code, CODE_LENGTH, CodeValidationError.TOO_SHORT)
        }
}
