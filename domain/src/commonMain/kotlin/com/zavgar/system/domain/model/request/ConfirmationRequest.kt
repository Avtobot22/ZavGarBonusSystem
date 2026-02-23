package com.zavgar.system.domain.model.request

data class ConfirmationRequest(
    val phone: String,
    val code: String,
    val isRegistration: Boolean
)
