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
import com.zavgar.system.domain.model.validation.BirthDateValidationError as DomainBirthDateValidationError
import com.zavgar.system.domain.model.validation.CodeValidationError as DomainCodeValidationError
import com.zavgar.system.domain.model.validation.NameValidationError as DomainNameValidationError
import com.zavgar.system.domain.model.validation.PasswordValidationError as DomainPasswordValidationError
import com.zavgar.system.domain.model.validation.PhoneValidationError as DomainPhoneValidationError
import com.zavgar.system.domain.model.validation.RepeatPasswordValidationError as DomainRepeatPasswordValidationError
import com.zavgar.system.domain.model.validation.ValidationResult as DomainValidationResult
import com.zavgar.system.utils.validation.ValidationResult as PresentationValidationResult

fun DomainPhoneValidationError.asUiText() = when (this) {
    DomainPhoneValidationError.Blank -> UiText.Resource(Res.string.error_blank_phone)
    DomainPhoneValidationError.InvalidFormat -> UiText.Resource(Res.string.error_invalid_format_phone)
    DomainPhoneValidationError.InvalidLength -> UiText.Resource(Res.string.error_invalid_length_phone)
}

fun DomainPasswordValidationError.asUiText() = when (this) {
    DomainPasswordValidationError.Blank -> UiText.Resource(Res.string.error_blank_password)
    DomainPasswordValidationError.Short -> UiText.Resource(Res.string.error_short_password)
}

fun DomainNameValidationError.asUiText() = when (this) {
    DomainNameValidationError.Blank -> UiText.Resource(Res.string.error_blank_name)
}

fun DomainBirthDateValidationError.asUiText() = when (this) {
    DomainBirthDateValidationError.Blank -> UiText.Resource(Res.string.error_blank_birth_date)
}

fun DomainRepeatPasswordValidationError.asUiText() = when (this) {
    DomainRepeatPasswordValidationError.NotMatching -> UiText.Resource(Res.string.error_passwords_not_match)
}

fun DomainCodeValidationError.asUiText() = when (this) {
    DomainCodeValidationError.Blank -> UiText.Resource(Res.string.error_blank_code)
    DomainCodeValidationError.TooShort -> UiText.Resource(Res.string.error_short_code)
}

fun <D, P> DomainValidationResult<D>.toPresentation(mapper: (D) -> P): PresentationValidationResult<P> {
    return when (this) {
        is DomainValidationResult.Success -> PresentationValidationResult.Success
        is DomainValidationResult.Error -> PresentationValidationResult.Error(mapper(this.error))
    }
}
