package com.zavgar.system.core.presentation.util

import com.zavgar.system.resources.Res
import com.zavgar.system.resources.error_blank_birth_date
import com.zavgar.system.resources.error_blank_code
import com.zavgar.system.resources.error_blank_name
import com.zavgar.system.resources.error_blank_phone
import com.zavgar.system.resources.error_invalid_format_phone
import com.zavgar.system.resources.error_invalid_length_phone
import com.zavgar.system.resources.error_short_code
import com.zavgar.system.utils.validation.BirthDateValidationError
import com.zavgar.system.utils.validation.CodeValidationError
import com.zavgar.system.utils.validation.NameValidationError
import com.zavgar.system.utils.validation.PhoneValidationError

/**
 * Presentation-мапперы доменных ошибок валидации в [UiText].
 *
 * Живут в `:core` (presentation), а не в `:utils:validation`, чтобы доменная валидация
 * оставалась чистой (без Compose-ресурсов) и переносимой на бэкенд/чистый JVM.
 */
fun PhoneValidationError.asUiText(): UiText = when (this) {
    PhoneValidationError.BLANK -> UiText.Resource(Res.string.error_blank_phone)
    PhoneValidationError.INVALID_FORMAT -> UiText.Resource(Res.string.error_invalid_format_phone)
    PhoneValidationError.INVALID_LENGTH -> UiText.Resource(Res.string.error_invalid_length_phone)
}

fun NameValidationError.asUiText(): UiText = when (this) {
    NameValidationError.BLANK -> UiText.Resource(Res.string.error_blank_name)
}

fun BirthDateValidationError.asUiText(): UiText = when (this) {
    BirthDateValidationError.BLANK -> UiText.Resource(Res.string.error_blank_birth_date)
}

fun CodeValidationError.asUiText(): UiText = when (this) {
    CodeValidationError.BLANK -> UiText.Resource(Res.string.error_blank_code)
    CodeValidationError.TOO_SHORT -> UiText.Resource(Res.string.error_short_code)
}
