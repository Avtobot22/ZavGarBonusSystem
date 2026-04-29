package com.zavgar.system.utils.validation


class ValidateRepeatedPasswordUseCase {

    operator fun invoke(password: String, repeatedPassword: String): ValidationResult<RepeatPasswordValidationError> {
        if (password != repeatedPassword) {
            return ValidationResult.Error(RepeatPasswordValidationError.NotMatching)
        }

        return ValidationResult.Success
    }
}