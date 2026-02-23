package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    @SerialName("phone")
    val phone: String,
    @SerialName("newPassword")
    val password: String
)
