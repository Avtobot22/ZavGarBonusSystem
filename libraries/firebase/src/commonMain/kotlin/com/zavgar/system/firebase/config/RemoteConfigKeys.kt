package com.zavgar.system.firebase.config

/**
 * Ключи параметров Firebase Remote Config.
 *
 * Названия должны совпадать с параметрами в консоли Firebase.
 */
object RemoteConfigKeys {
    const val BASE_URL: String = "base_url"
    const val PRIVACY_POLICY_URL: String = "privacy_policy_url"
    const val TEST_ACCOUNT_ENABLED: String = "test_account_enabled"
    const val TEST_PHONE_NUMBER: String = "test_phone_number"
    const val TEST_OTP_CODE: String = "test_otp_code"
    const val MIN_SUPPORTED_VERSION: String = "min_supported_version"

    /** Все ключи — для диагностики/перебора. */
    val ALL: List<String> = listOf(
        BASE_URL,
        PRIVACY_POLICY_URL,
        TEST_ACCOUNT_ENABLED,
        TEST_PHONE_NUMBER,
        TEST_OTP_CODE,
        MIN_SUPPORTED_VERSION,
    )
}
