package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmationRequest(
    @SerialName("phone")
    val phone: String,
    @SerialName("code")
    val code: String,
)
