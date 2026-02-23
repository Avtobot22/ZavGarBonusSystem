package com.zavgar.system.repository.model.request


data class ConfirmationRequest(
    val phone: String,
    val code: String
)
