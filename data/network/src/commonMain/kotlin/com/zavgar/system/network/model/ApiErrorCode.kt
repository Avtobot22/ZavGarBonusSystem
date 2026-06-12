package com.zavgar.system.network.model

enum class ApiErrorCode {
    VALIDATION_ERROR,
    INVALID_OPERATION,
    INVALID_CREDENTIALS,
    INVALID_CONFIRMATION_CODE,
    INVALID_FORMAT,
    INVALID_ARGUMENT,
    INVALID_TOKEN,
    INVALID_MASTER_PASSWORD,
    REFRESH_FAILED,
    ACCESS_DENIED,
    NOT_FOUND,
    ALREADY_EXISTS,
    DUPLICATE_RESOURCE,
    CONFIRMATION_CODE_EXPIRED,
    AUTH_SESSION_EXPIRED,
    BUSINESS_ERROR,
    TOO_MANY_REQUESTS,
    CONFIRMATION_ATTEMPTS_EXCEEDED,
    DATABASE_ERROR,
    INTERNAL_ERROR,
    ;

    companion object {
        fun fromRaw(raw: String?): ApiErrorCode? =
            raw?.let { value -> entries.firstOrNull { it.name == value } }
    }
}
