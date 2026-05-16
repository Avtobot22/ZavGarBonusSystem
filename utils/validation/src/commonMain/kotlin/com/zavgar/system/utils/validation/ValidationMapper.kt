package com.zavgar.system.utils.validation

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_blank_birth_date
import com.zavgar.system.resources.error_blank_code
import com.zavgar.system.resources.error_blank_name
import com.zavgar.system.resources.error_blank_phone
import com.zavgar.system.resources.error_invalid_format_phone
import com.zavgar.system.resources.error_invalid_length_phone
import com.zavgar.system.resources.error_short_code

fun PhoneValidationError.asUiText() = when (this) {
    PhoneValidationError.BLANK -> UiText.Resource(Res.string.error_blank_phone)
    PhoneValidationError.INVALID_FORMAT -> UiText.Resource(Res.string.error_invalid_format_phone)
    PhoneValidationError.INVALID_LENGTH -> UiText.Resource(Res.string.error_invalid_length_phone)
}

fun NameValidationError.asUiText() = when (this) {
    NameValidationError.BLANK -> UiText.Resource(Res.string.error_blank_name)
}

fun BirthDateValidationError.asUiText() = when (this) {
    BirthDateValidationError.BLANK -> UiText.Resource(Res.string.error_blank_birth_date)
}

fun CodeValidationError.asUiText() = when (this) {
    CodeValidationError.BLANK -> UiText.Resource(Res.string.error_blank_code)
    CodeValidationError.TOO_SHIRT -> UiText.Resource(Res.string.error_short_code)
}

fun <D, P> ValidationResult<D>.toPresentation(mapper: (D) -> P): ValidationResult<P> {
    return when (this) {
        is ValidationResult.Valid -> ValidationResult.Valid
        is ValidationResult.Invalid -> ValidationResult.Invalid(mapper(this.error))
    }
}
