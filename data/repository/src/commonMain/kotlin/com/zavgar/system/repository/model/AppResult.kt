package com.zavgar.system.repository.model

/**
 * Represents the result of an app operation.
 *
 * @param T the type of the successful result
 * @param E the type of the error result
 *
 */
sealed interface AppResult<out T, out E> {

    data class Success<out T>(val data: T) : AppResult<T, Nothing>

    data class Error<out E>(val error: E) : AppResult<Nothing, E>
}