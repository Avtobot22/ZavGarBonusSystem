package com.zavgar.system.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Единый формат тела ошибки прикладного уровня (см. схему `ErrorResponse` в Api.yaml).
 *
 * Клиент должен ветвить логику по машиночитаемому полю [code] (см. [ApiErrorCode]),
 * а не по тексту [message] (может меняться) и не по одному лишь HTTP-статусу:
 * под одним статусом сервер возвращает несколько разных кодов.
 */
@Serializable
data class ErrorResponse(
    @SerialName("code")
    val code: String,
    @SerialName("message")
    val message: String,
    @SerialName("details")
    val details: Map<String, String>? = null,
    @SerialName("requestId")
    val requestId: String? = null,
)
