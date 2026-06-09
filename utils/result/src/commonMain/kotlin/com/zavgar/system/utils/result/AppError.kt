package com.zavgar.system.utils.result

sealed interface AppError {
    interface TooManyRequest : AppError {
        /** Значение заголовка `Retry-After` в секундах, если сервер его прислал; иначе `null`. */
        val retryAfterSeconds: Long?
    }

    interface Server : AppError
    interface Network : AppError
    interface Unknown : AppError {
        val message: String
    }
}
