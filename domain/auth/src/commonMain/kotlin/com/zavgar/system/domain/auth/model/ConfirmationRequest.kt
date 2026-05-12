package com.zavgar.system.domain.auth.model

data class ConfirmationRequest(
    val phone: String,
    val code: String,
    val isRegistration: Boolean,
)
