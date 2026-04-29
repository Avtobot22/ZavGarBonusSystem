package com.zavgar.system.utils.validation

import kotlinx.datetime.LocalDate

class ValidateBirthDateUseCase {

    operator fun invoke(birthDate: LocalDate?): ValidationResult<BirthDateValidationError> {
        if (birthDate == null) {
            return ValidationResult.Error(BirthDateValidationError.Blank)
        }

        return ValidationResult.Success
    }
}