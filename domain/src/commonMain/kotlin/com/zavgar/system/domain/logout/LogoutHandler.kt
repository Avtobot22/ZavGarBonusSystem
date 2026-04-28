package com.zavgar.system.domain.logout

import com.zavgar.system.domain.usecase.DeleteSessionUseCase
import com.zavgar.system.events.AppEvent
import com.zavgar.system.events.AppEventBus

class LogoutHandler(
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val appEventBus: AppEventBus,
) {
    suspend fun logout() {
        deleteSessionUseCase().onFailure { /* session may already be gone */ }
        appEventBus.emit(AppEvent.Logout)
    }
}
