package com.zavgar.system.domain.usecase.validation

import com.zavgar.system.domain.model.validation.PhoneValidationError
import com.zavgar.system.domain.model.validation.ValidationResult

class ValidatePhoneUseCase {

    operator fun invoke(phone: String): ValidationResult<PhoneValidationError> {

        if (phone.isBlank()) {
            return ValidationResult.Error(PhoneValidationError.Blank)
        }

        if (!phone.all { it.isDigit() }) {
            return ValidationResult.Error(PhoneValidationError.InvalidFormat)
        }

        if (phone.length != 10) {
            return ValidationResult.Error(PhoneValidationError.InvalidLength)
        }

        return ValidationResult.Success
    }
}