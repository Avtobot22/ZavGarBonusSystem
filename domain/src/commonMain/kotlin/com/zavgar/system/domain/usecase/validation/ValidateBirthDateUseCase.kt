package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.BirthDateValidationError
import com.zavgar.system.domain.model.validation.ValidationResult
import kotlinx.datetime.LocalDate

class ValidateBirthDateUseCase {

    operator fun invoke(birthDate: LocalDate?): ValidationResult<BirthDateValidationError> {
        if (birthDate == null) {
            return ValidationResult.Error(BirthDateValidationError.Blank)
        }

        return ValidationResult.Success
    }
}