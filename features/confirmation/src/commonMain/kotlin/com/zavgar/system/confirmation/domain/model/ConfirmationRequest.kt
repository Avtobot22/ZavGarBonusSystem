package com.zavgar.system.confirmation.domain.model

data class ConfirmationRequest(
    val phone: String,
    val code: String,
    val isRegistration: Boolean,
)
