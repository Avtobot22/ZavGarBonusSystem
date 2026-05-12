package com.zavgar.system.utils.result

sealed interface AppError {
    interface TooManyRequest : AppError
    interface Server : AppError
    interface Network : AppError
    interface Unknown : AppError {
        val message: String
    }
}
