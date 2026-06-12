package com.zavgar.system.firebase.config

import com.zavgar.system.config.AppConfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.FirebaseRemoteConfig
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Firebase Remote Config-реализация [AppConfig].
 */
internal class RemoteConfigServiceImpl(
    private val isDebugBuild: Boolean,
) : AppConfig {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    override suspend fun activate() {
        try {
            withTimeoutOrNull(ACTIVATION_TIMEOUT) {
                remoteConfig.settings {
                    minimumFetchInterval =
                        if (isDebugBuild) Duration.ZERO else RELEASE_FETCH_INTERVAL
                    fetchTimeout = FETCH_TIMEOUT
                }
                remoteConfig.setDefaults(*defaults())
                remoteConfig.fetchAndActivate()
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
        }
    }

    override val baseUrl: String
        // В debug всегда локальный адрес (10.0.2.2 / localhost), Remote Config игнорируется.
        // В release base_url берётся из Remote Config, дефолт — на случай оффлайна.
        get() = if (isDebugBuild) {
            defaultBaseUrl
        } else {
            stringValue(RemoteConfigKeys.BASE_URL).ifBlank { defaultBaseUrl }
        }

    override val privacyPolicyUrl: String
        get() = stringValue(RemoteConfigKeys.PRIVACY_POLICY_URL).ifBlank { DEFAULT_PRIVACY_POLICY_URL }

    override val testAccountEnabled: Boolean
        get() = runCatching {
            remoteConfig.get<Boolean>(RemoteConfigKeys.TEST_ACCOUNT_ENABLED)
        }.getOrDefault(false)

    override val testPhoneNumber: String
        get() = stringValue(RemoteConfigKeys.TEST_PHONE_NUMBER)

    override val testOtpCode: String
        get() = stringValue(RemoteConfigKeys.TEST_OTP_CODE)

    override val minSupportedVersion: String
        get() = stringValue(RemoteConfigKeys.MIN_SUPPORTED_VERSION)

    private fun stringValue(key: String): String =
        runCatching { remoteConfig.get<String>(key) }.getOrDefault("")

    /**
     * Дефолтные значения в коде. ВАЖНО: [RemoteConfigKeys.TEST_ACCOUNT_ENABLED]
     * всегда `false` — тестовый аккаунт включается только из консоли Firebase.
     */
    private fun defaults(): Array<Pair<String, Any?>> = arrayOf(
        RemoteConfigKeys.BASE_URL to defaultBaseUrl,
        RemoteConfigKeys.PRIVACY_POLICY_URL to DEFAULT_PRIVACY_POLICY_URL,
        RemoteConfigKeys.TEST_ACCOUNT_ENABLED to false,
        RemoteConfigKeys.TEST_PHONE_NUMBER to "",
        RemoteConfigKeys.TEST_OTP_CODE to "",
        RemoteConfigKeys.MIN_SUPPORTED_VERSION to "",
    )

    private companion object {
        val RELEASE_FETCH_INTERVAL: Duration = 3600.seconds
        val FETCH_TIMEOUT: Duration = 10.seconds
        val ACTIVATION_TIMEOUT: Duration = 8.seconds
        const val DEFAULT_PRIVACY_POLICY_URL =
            "https://zavgar.ru/n/v/275/soglasie-na-obrabotku-personalnyh-dannyh-polzovatela"
    }
}
