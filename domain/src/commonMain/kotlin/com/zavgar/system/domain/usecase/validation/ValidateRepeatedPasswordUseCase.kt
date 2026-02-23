package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.RepeatPasswordValidationError
import com.zavgar.system.domain.model.validation.ValidationResult

class ValidateRepeatedPasswordUseCase {

    operator fun invoke(password: String, repeatedPassword: String): ValidationResult<RepeatPasswordValidationError> {
        if (password != repeatedPassword) {
            return ValidationResult.Error(RepeatPasswordValidationError.NotMatching)
        }

        return ValidationResult.Success
    }
}