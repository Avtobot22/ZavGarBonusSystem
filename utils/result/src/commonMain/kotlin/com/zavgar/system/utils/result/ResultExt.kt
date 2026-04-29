package com.zavgar.system.utils.result

inline fun <T, E> Result<T>.toAppResult(errorMapper: (Throwable) -> E): AppResult<T, E> =
    fold(
        onSuccess = { AppResult.Success(it) },
        onFailure = { AppResult.Error(errorMapper(it)) }
    )
