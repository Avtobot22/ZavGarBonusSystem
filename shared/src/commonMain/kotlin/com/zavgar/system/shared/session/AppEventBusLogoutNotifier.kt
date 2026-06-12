package com.zavgar.system.shared.session

import com.zavgar.system.domain.session.LogoutNotifier
import com.zavgar.system.events.AppEvent
import com.zavgar.system.events.AppEventBus

/**
 * Реализация доменного порта [LogoutNotifier] поверх инфраструктурной шины [AppEventBus].
 *
 * Живёт в композиционном слое (`:shared`), чтобы домен (`:domain:session`) не зависел от
 * `:libraries:events`.
 */
internal class AppEventBusLogoutNotifier(
    private val appEventBus: AppEventBus,
) : LogoutNotifier {
    override fun notifyLoggedOut() {
        appEventBus.emit(AppEvent.Logout)
    }
}
