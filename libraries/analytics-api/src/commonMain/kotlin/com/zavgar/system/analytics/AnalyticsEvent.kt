package com.zavgar.system.analytics

/**
 * Типобезопасные события аналитики. Каждое событие знает своё имя и параметры.
 *
 * Имена и ключи — snake_case, латиница, ≤ 40 символов (общепринятое ограничение
 * провайдеров аналитики).
 */
sealed interface AnalyticsEvent {

    val name: String

    val params: Map<String, Any?>
        get() = emptyMap()

    // --- Onboarding ---

    data object OnboardingStart : AnalyticsEvent {
        override val name: String = "onboarding_start"
    }

    data class OnboardingStepView(val stepIndex: Int) : AnalyticsEvent {
        override val name: String = "onboarding_step_view"
        override val params: Map<String, Any?> = mapOf("step_index" to stepIndex)
    }

    data object OnboardingComplete : AnalyticsEvent {
        override val name: String = "onboarding_complete"
        override val params: Map<String, Any?> = mapOf("completed" to true)
    }

    data class OnboardingSkip(val stepIndex: Int) : AnalyticsEvent {
        override val name: String = "onboarding_skip"
        override val params: Map<String, Any?> = mapOf("step_index" to stepIndex)
    }

    // --- Auth ---

    data class OtpRequested(val flow: AuthFlow) : AnalyticsEvent {
        override val name: String = "otp_requested"
        override val params: Map<String, Any?> = mapOf("flow" to flow.value)
    }

    data class AuthLinkClick(val target: AuthFlow) : AnalyticsEvent {
        override val name: String = "auth_link_click"
        override val params: Map<String, Any?> = mapOf("target" to target.value)
    }

    data class OtpResend(val flow: AuthFlow) : AnalyticsEvent {
        override val name: String = "otp_resend"
        override val params: Map<String, Any?> = mapOf("flow" to flow.value)
    }

    data class OtpVerifyError(val flow: AuthFlow, val errorType: String) : AnalyticsEvent {
        override val name: String = "otp_verify_error"
        override val params: Map<String, Any?> = mapOf(
            "flow" to flow.value,
            "error_type" to errorType,
        )
    }

    /** Предопределённое событие провайдера `login`. */
    data object LoginSuccess : AnalyticsEvent {
        override val name: String = "login"
        override val params: Map<String, Any?> = mapOf("method" to METHOD_PHONE_OTP)
    }

    /** Предопределённое событие провайдера `sign_up`. */
    data object SignUpSuccess : AnalyticsEvent {
        override val name: String = "sign_up"
        override val params: Map<String, Any?> = mapOf("method" to METHOD_PHONE_OTP)
    }

    // --- Wallet (баланс как значение НЕ передаём) ---

    data object WalletBalanceViewed : AnalyticsEvent {
        override val name: String = "wallet_balance_viewed"
    }

    data class WalletBalanceError(val errorType: String) : AnalyticsEvent {
        override val name: String = "wallet_balance_error"
        override val params: Map<String, Any?> = mapOf("error_type" to errorType)
    }

    // --- History ---

    data class HistoryViewed(val itemsCount: Int) : AnalyticsEvent {
        override val name: String = "history_viewed"
        override val params: Map<String, Any?> = mapOf("items_count" to itemsCount)
    }

    data class HistoryLoadMore(val page: Int) : AnalyticsEvent {
        override val name: String = "history_load_more"
        override val params: Map<String, Any?> = mapOf("page" to page)
    }

    data class TransactionClick(val transactionType: String) : AnalyticsEvent {
        override val name: String = "transaction_click"
        override val params: Map<String, Any?> = mapOf("transaction_type" to transactionType)
    }

    // --- Bottom navigation ---

    data class TabSelected(val tabName: String) : AnalyticsEvent {
        override val name: String = "tab_selected"
        override val params: Map<String, Any?> = mapOf("tab_name" to tabName)
    }

    // --- Settings / Account ---

    data object SettingsOpened : AnalyticsEvent {
        override val name: String = "settings_opened"
    }

    data object ProfileOpened : AnalyticsEvent {
        override val name: String = "profile_opened"
    }

    data object Logout : AnalyticsEvent {
        override val name: String = "logout"
        override val params: Map<String, Any?> = mapOf("source" to "settings")
    }

    data object DeleteAccount : AnalyticsEvent {
        override val name: String = "delete_account"
        override val params: Map<String, Any?> = mapOf("confirmed" to true)
    }

    // --- Network ---

    data class ApiError(val endpoint: String, val code: String) : AnalyticsEvent {
        override val name: String = "api_error"
        override val params: Map<String, Any?> = mapOf(
            "endpoint" to endpoint,
            "code" to code,
        )
    }

    companion object {
        const val METHOD_PHONE_OTP: String = "phone_otp"
    }
}

/** Сценарий аутентификации — отличает login от registration в общих событиях. */
enum class AuthFlow(val value: String) {
    LOGIN("login"),
    REGISTRATION("registration"),
}
