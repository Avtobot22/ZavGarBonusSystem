package com.zavgar.system.domain.model.validation

sealed interface PhoneValidationError {

    data object Blank : PhoneValidationError

    data object InvalidLength : PhoneValidationError

    data object InvalidFormat : PhoneValidationError
}