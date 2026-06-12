package com.zavgar.system.domain.session

import com.zavgar.system.domain.session.usecase.DeleteSessionUseCase

class LogoutHandler(
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val logoutNotifier: LogoutNotifier,
) {
    suspend fun logout() {
        deleteSessionUseCase()
        logoutNotifier.notifyLoggedOut()
    }
}
