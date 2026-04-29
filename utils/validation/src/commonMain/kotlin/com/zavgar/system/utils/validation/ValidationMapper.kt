package com.zavgar.system.utils.validation

import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_blank_birth_date
import com.zavgar.system.resources.error_blank_code
import com.zavgar.system.resources.error_blank_name
import com.zavgar.system.resources.error_blank_password
import com.zavgar.system.resources.error_blank_phone
import com.zavgar.system.resources.error_invalid_format_phone
import com.zavgar.system.resources.error_invalid_length_phone
import com.zavgar.system.resources.error_passwords_not_match
import com.zavgar.system.resources.error_short_code
import com.zavgar.system.resources.error_short_password

fun PhoneValidationError.asUiText() = when (this) {
    PhoneValidationError.Blank -> UiText.Resource(Res.string.error_blank_phone)
    PhoneValidationError.InvalidFormat -> UiText.Resource(Res.string.error_invalid_format_phone)
    PhoneValidationError.InvalidLength -> UiText.Resource(Res.string.error_invalid_length_phone)
}

fun PasswordValidationError.asUiText() = when (this) {
    PasswordValidationError.Blank -> UiText.Resource(Res.string.error_blank_password)
    PasswordValidationError.Short -> UiText.Resource(Res.string.error_short_password)
}

fun NameValidationError.asUiText() = when (this) {
    NameValidationError.Blank -> UiText.Resource(Res.string.error_blank_name)
}

fun BirthDateValidationError.asUiText() = when (this) {
    BirthDateValidationError.Blank -> UiText.Resource(Res.string.error_blank_birth_date)
}

fun RepeatPasswordValidationError.asUiText() = when (this) {
    RepeatPasswordValidationError.NotMatching -> UiText.Resource(Res.string.error_passwords_not_match)
}

fun CodeValidationError.asUiText() = when (this) {
    CodeValidationError.Blank -> UiText.Resource(Res.string.error_blank_code)
    CodeValidationError.TooShort -> UiText.Resource(Res.string.error_short_code)
}

fun <D, P> ValidationResult<D>.toPresentation(mapper: (D) -> P): ValidationResult<P> {
    return when (this) {
        is ValidationResult.Success -> ValidationResult.Success
        is ValidationResult.Error -> ValidationResult.Error(mapper(this.error))
    }
}
