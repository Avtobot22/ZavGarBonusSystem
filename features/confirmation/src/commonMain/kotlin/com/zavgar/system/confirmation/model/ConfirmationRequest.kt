package com.zavgar.system.confirmation.model

data class ConfirmationRequest(
    val phone: String,
    val code: String,
    val isRegistration: Boolean
)
