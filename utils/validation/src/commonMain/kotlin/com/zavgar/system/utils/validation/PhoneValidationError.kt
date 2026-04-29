package com.zavgar.system.utils.validation

sealed interface PhoneValidationError {

    data object Blank : PhoneValidationError

    data object InvalidLength : PhoneValidationError

    data object InvalidFormat : PhoneValidationError
}