package com.zavgar.system.confirmation.presentation

import com.zavgar.system.analytics.AnalyticsEvent
import com.zavgar.system.analytics.AnalyticsTracker
import com.zavgar.system.analytics.AuthFlow
import com.zavgar.system.config.AppConfig
import com.zavgar.system.confirmation.mapper.asSnackBarMessage
import com.zavgar.system.confirmation.mapper.toConfirmationResult
import com.zavgar.system.confirmation.mapper.toResendConfirmationResult
import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.core.presentation.util.asUiText
import com.zavgar.system.domain.auth.model.ConfirmationRequest
import com.zavgar.system.domain.auth.model.ResendRequest
import com.zavgar.system.domain.auth.usecase.ConfirmationUseCase
import com.zavgar.system.domain.auth.usecase.ResendCodeUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.confirmation_resend_success
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.utils.result.AppError
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.toPresentation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class ConfirmationViewModel(
    private val validateCodeUseCase: ValidateCodeUseCase,
    private val confirmationUseCase: ConfirmationUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    private val appConfig: AppConfig,
    private val analyticsTracker: AnalyticsTracker,
) : BaseViewModel<ConfirmationState, ConfirmationIntent, ConfirmationEvent>(ConfirmationState()) {

    private val authFlow: AuthFlow
        get() = if (currentState.isRegistration) AuthFlow.REGISTRATION else AuthFlow.LOGIN

    private var timerJob: Job? = null
    private var isInitialized = false

    companion object {
        private const val TIMER_DURATION_SECONDS = 60
        private const val PHONE_COMPARE_DIGITS = 10
        private const val SECOND_MILLIS = 1000L
    }

    override fun handleIntent(intent: ConfirmationIntent) {
        when (intent) {
            is ConfirmationIntent.Initialize -> handleInitialize(intent.phone, intent.isRegistration)
            is ConfirmationIntent.EnterCode -> handleEnterCode(intent.code)
            is ConfirmationIntent.Submit -> handleSubmit()
            is ConfirmationIntent.ClickResend -> handleResend()
        }
    }

    private fun handleInitialize(phone: String, isRegistration: Boolean) {
        setState {
            copy(
                phone = phone,
                isRegistration = isRegistration,
            )
        }
        if (isInitialized) return

        isInitialized = true
        startTimer()
        autofillTestCodeIfNeeded(phone)
    }

    /**
     * Если включён тестовый аккаунт и [phone] совпадает с тестовым номером —
     * подставляет тестовый OTP и автоматически отправляет его (автопрохождение верификации).
     */
    private fun autofillTestCodeIfNeeded(phone: String) {
        if (!appConfig.testAccountEnabled) return
        if (!phonesMatch(phone, appConfig.testPhoneNumber)) return

        val testCode = appConfig.testOtpCode
        if (testCode.isBlank()) return

        setState { copy(code = testCode, codeError = null) }
        handleSubmit()
    }

    private fun phonesMatch(first: String, second: String): Boolean {
        fun normalize(value: String) = value.filter(Char::isDigit).takeLast(PHONE_COMPARE_DIGITS)
        val normalizedFirst = normalize(first)
        return normalizedFirst.isNotEmpty() && normalizedFirst == normalize(second)
    }

    private fun handleEnterCode(code: String) = setState {
        copy(
            code = code,
            codeError = null,
        )
    }

    private fun handleSubmit() {
        if (currentState.screenState is ConfirmationState.ScreenState.Submitting) return
        val isValid = validateCodeUseCase(currentState.code).toPresentation { it.asUiText() }

        if (isValid is ValidationResult.Invalid) {
            setState { copy(codeError = isValid.error) }
            return
        }

        performConfirmation(ConfirmationRequest(currentState.phone, currentState.code, currentState.isRegistration))
    }

    private fun handleResend() {
        val isSubmitting = currentState.screenState is ConfirmationState.ScreenState.Submitting
        if (currentState.timerSeconds > 0 || isSubmitting) return

        launchTry {
            analyticsTracker.log(AnalyticsEvent.OtpResend(authFlow))

            val appResult = resendCodeUseCase(ResendRequest(currentState.phone))

            when (appResult) {
                is AppResult.Success -> startTimer()
                is AppResult.Error -> {
                    val error = appResult.error
                    if (error is AppError.TooManyRequest) {
                        startTimer(error.retryAfterSeconds?.toInt()?.takeIf { it > 0 } ?: TIMER_DURATION_SECONDS)
                    }
                }
            }

            when (val result = appResult.toResendConfirmationResult { it.asSnackBarMessage() }) {
                is ResendConfirmationResult.Success -> setEvent {
                    ConfirmationEvent.ShowSnackbar(
                        SnackBarMessage(
                            message = UiText.Resource(Res.string.confirmation_resend_success),
                            type = SnackBarType.SUCCESS,
                        ),
                    )
                }

                is ResendConfirmationResult.Error -> setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
            }
        } catch {
            setEvent {
                ConfirmationEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }

    private fun performConfirmation(confirmationRequest: ConfirmationRequest) {
        launchTry {
            setState { copy(screenState = ConfirmationState.ScreenState.Submitting) }

            val result =
                confirmationUseCase(confirmationRequest).toConfirmationResult { it.asSnackBarMessage() }

            when (result) {
                is ConfirmationResult.Success -> {
                    if (confirmationRequest.isRegistration) {
                        analyticsTracker.log(AnalyticsEvent.SignUpSuccess)
                    } else {
                        analyticsTracker.log(AnalyticsEvent.LoginSuccess)
                    }
                    setEvent {
                        ConfirmationEvent.NavigateToWallet
                    }
                }

                is ConfirmationResult.Error -> {
                    analyticsTracker.log(
                        AnalyticsEvent.OtpVerifyError(authFlow, errorType = "verify_failed"),
                    )
                    setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
                }
            }

            setState { copy(screenState = ConfirmationState.ScreenState.Idle) }
        } catch {
            setState { copy(screenState = ConfirmationState.ScreenState.Idle) }
            setEvent {
                ConfirmationEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error)),
                )
            }
        }
    }

    private fun startTimer(durationSeconds: Int = TIMER_DURATION_SECONDS) {
        timerJob?.cancel()
        setState { copy(timerSeconds = durationSeconds) }
        timerJob = launchTry {
            for (seconds in (durationSeconds - 1) downTo 0) {
                delay(SECOND_MILLIS.milliseconds)
                setState { copy(timerSeconds = seconds) }
            }
        } catch {
            // ignore — timer never produces real errors
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
