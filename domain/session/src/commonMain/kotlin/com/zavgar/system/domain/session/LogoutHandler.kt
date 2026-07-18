package com.zavgar.system.domain.session

import com.zavgar.system.domain.session.usecase.DeleteSessionIfCurrentUseCase
import com.zavgar.system.domain.session.usecase.DeleteSessionUseCase
import com.zavgar.system.utils.result.AppResult

class LogoutHandler(
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val deleteSessionIfCurrentUseCase: DeleteSessionIfCurrentUseCase,
    private val logoutNotifier: LogoutNotifier,
) {
    suspend fun logout() {
        deleteSessionUseCase()
        logoutNotifier.notifyLoggedOut()
    }

    suspend fun logoutIfCurrent(refreshToken: String) {
        val result = deleteSessionIfCurrentUseCase(refreshToken)
        if (result is AppResult.Success && result.data) {
            logoutNotifier.notifyLoggedOut()
        }
    }
}
