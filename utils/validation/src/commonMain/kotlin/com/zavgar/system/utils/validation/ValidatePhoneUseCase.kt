package com.zavgar.system.utils.validation

class ValidatePhoneUseCase {

    operator fun invoke(phone: String): ValidationResult<PhoneValidationError> =
        validate {
            notBlank(phone, PhoneValidationError.BLANK)
            digitsOnly(phone, PhoneValidationError.INVALID_FORMAT)
            exactLength(phone, PHONE_LENGTH, PhoneValidationError.INVALID_LENGTH)
        }
}
