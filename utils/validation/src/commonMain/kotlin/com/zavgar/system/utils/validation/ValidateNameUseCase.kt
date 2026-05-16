package com.zavgar.system.utils.validation

class ValidateNameUseCase {

    operator fun invoke(name: String): ValidationResult<NameValidationError> =
        validate {
            notBlank(name, NameValidationError.BLANK)
        }
}
