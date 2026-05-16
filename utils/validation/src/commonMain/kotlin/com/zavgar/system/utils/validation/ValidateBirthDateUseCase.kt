package com.zavgar.system.utils.validation

import kotlinx.datetime.LocalDate

class ValidateBirthDateUseCase {

    operator fun invoke(birthDate: LocalDate?): ValidationResult<BirthDateValidationError> =
        validate {
            check(BirthDateValidationError.BLANK) { birthDate != null }
        }
}
