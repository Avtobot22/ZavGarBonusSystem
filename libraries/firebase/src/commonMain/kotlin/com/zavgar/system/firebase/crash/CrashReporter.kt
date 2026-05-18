package com.zavgar.system.firebase.crash

/**
 * Обёртка над Firebase Crashlytics для общего кода.
 */
interface CrashReporter {

    /** Записать нефатальное исключение. */
    fun recordException(throwable: Throwable)

    /** Добавить сообщение в лог отчёта о падении. */
    fun log(message: String)

    /** Установить произвольный ключ для отчёта. */
    fun setCustomKey(key: String, value: String)

    /** Связать отчёты с идентификатором пользователя. */
    fun setUserId(userId: String)
}
