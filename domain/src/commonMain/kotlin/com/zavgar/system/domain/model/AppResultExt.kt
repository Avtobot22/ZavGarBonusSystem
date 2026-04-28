package com.zavgar.system.domain.model

import com.zavgar.system.domain.logout.LogoutHandler
import com.zavgar.system.domain.model.error.NotAuthorized

suspend fun <T, E> AppResult<T, E>.onTokenExpired(
    logoutHandler: LogoutHandler,
): AppResult<T, E>? {
    if (this is AppResult.Error && error is NotAuthorized) {
        logoutHandler.logout()
        return null
    }
    return this
}
