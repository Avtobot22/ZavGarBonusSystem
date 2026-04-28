package com.zavgar.system.repository.util

import com.zavgar.system.domain.model.AppResult

internal fun <T, E> Result<T>.toRepoResult(errorMapper: (Throwable) -> E): AppResult<T, E> =
    fold(
        onSuccess = { AppResult.Success(it) },
        onFailure = { AppResult.Error(errorMapper(it)) }
    )
