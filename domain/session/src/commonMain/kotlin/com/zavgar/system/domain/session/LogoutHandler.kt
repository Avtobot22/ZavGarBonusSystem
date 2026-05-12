package com.zavgar.system.domain.session

import com.zavgar.system.domain.session.usecase.DeleteSessionUseCase
import com.zavgar.system.events.AppEvent
import com.zavgar.system.events.AppEventBus

class LogoutHandler(
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val appEventBus: AppEventBus,
) {
    suspend fun logout() {
        deleteSessionUseCase()
        appEventBus.emit(AppEvent.Logout)
    }
}
