package com.zavgar.system.firebase.config

/**
 * Доступ к параметрам Firebase Remote Config из общего кода.
 *
 * [activate] нужно вызвать на старте приложения (до инициализации сетевого слоя),
 * чтобы геттеры отдавали актуальные серверные значения.
 */
interface RemoteConfigService {

    /**
     * Применяет настройки, регистрирует дефолты, фетчит и активирует значения.
     *
     * Безопасно при оффлайне — при ошибке фетча используются последние активированные
     * или дефолтные значения.
     */
    suspend fun activate()

    /** Базовый URL сервера. */
    val baseUrl: String

    /** URL политики обработки персональных данных. */
    val privacyPolicyUrl: String

    /** Включён ли тестовый аккаунт (для автопрохождения верификации). */
    val testAccountEnabled: Boolean

    /** Номер телефона тестового аккаунта. */
    val testPhoneNumber: String

    /** OTP-код тестового аккаунта. */
    val testOtpCode: String

    /** Минимальная поддерживаемая версия приложения. */
    val minSupportedVersion: String
}
