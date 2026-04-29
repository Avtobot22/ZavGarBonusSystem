package com.zavgar.system.utils.result

sealed interface AppResult<out T, out E> {

    data class Success<out T>(val data: T) : AppResult<T, Nothing>

    data class Error<out E>(val error: E) : AppResult<Nothing, E>
}
