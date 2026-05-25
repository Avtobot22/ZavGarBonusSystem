package com.zavgar.system.firebase.analytics

/**
 * Обёртка над Firebase Analytics для общего кода.
 *
 * Аналитика включается сразу (без явного opt-in) — факт сбора покрыт политикой
 * конфиденциальности. PII в события не передаём.
 */
interface AnalyticsTracker {

    /** Залогировать типобезопасное событие. */
    fun log(event: AnalyticsEvent)

    /** Залогировать просмотр экрана (предопределённое событие `screen_view`). */
    fun logScreenView(screenName: String, screenClass: String? = null)

    /** Связать события с идентификатором пользователя (null — сбросить). */
    fun setUserId(userId: String?)

    /** Установить свойство пользователя (null — удалить). */
    fun setUserProperty(key: String, value: String?)

    /** Сбросить пользователя и связанные данные (при logout / удалении аккаунта). */
    fun clearUser()

    companion object {
        const val PROPERTY_ONBOARDING_COMPLETED: String = "onboarding_completed"
        const val PROPERTY_AUTH_METHOD: String = "auth_method"
    }
}
