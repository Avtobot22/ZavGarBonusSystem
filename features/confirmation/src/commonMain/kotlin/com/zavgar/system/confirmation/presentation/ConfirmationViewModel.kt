package com.zavgar.system.confirmation.presentation

import com.zavgar.system.confirmation.mapper.asSnackBarMessage
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.confirmation.mapper.toConfirmationResult
import com.zavgar.system.confirmation.mapper.toResendConfirmationResult
import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.domain.auth.model.ConfirmationRequest
import com.zavgar.system.domain.auth.model.ResendRequest
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.auth.usecase.ConfirmationUseCase
import com.zavgar.system.domain.auth.usecase.ResendCodeUseCase
import com.zavgar.system.firebase.config.RemoteConfigService
import com.zavgar.system.utils.validation.ValidateCodeUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.confirmation_resend_success
import com.zavgar.system.utils.validation.ValidationResult
import com.zavgar.system.utils.validation.asUiText
import com.zavgar.system.utils.validation.toPresentation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

class ConfirmationViewModel(
    private val validateCodeUseCase: ValidateCodeUseCase,
    private val confirmationUseCase: ConfirmationUseCase,
    private val resendCodeUseCase: ResendCodeUseCase,
    private val remoteConfigService: RemoteConfigService,
) : BaseViewModel<ConfirmationState, ConfirmationIntent, ConfirmationEvent>(ConfirmationState()) {

    init {
        startTimer()
    }

    private var timerJob: Job? = null

    companion object {
        private const val TIMER_DURATION_SECONDS = 60
        private const val PHONE_COMPARE_DIGITS = 10

        // TODO: временный диагностический префикс — убрать после проверки тестового аккаунта.
        private const val LOG_TAG = "RemoteConfigDebug"
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
                isRegistration = isRegistration
            )
        }
        autofillTestCodeIfNeeded(phone)
    }

    /**
     * Если включён тестовый аккаунт и [phone] совпадает с тестовым номером —
     * подставляет тестовый OTP и автоматически отправляет его (автопрохождение верификации).
     */
    private fun autofillTestCodeIfNeeded(phone: String) {
        // TODO: временный диагностический лог — убрать после проверки тестового аккаунта.
        val enabled = remoteConfigService.testAccountEnabled
        val testPhone = remoteConfigService.testPhoneNumber
        val testCode = remoteConfigService.testOtpCode
        val matches = phonesMatch(phone, testPhone)
        println(
            "$LOG_TAG: autofill — testAccountEnabled=$enabled, " +
                "phoneReceived=\"$phone\", testPhoneFromConfig=\"$testPhone\", " +
                "phonesMatch=$matches, testOtpCode=\"$testCode\""
        )

        if (!enabled) {
            println("$LOG_TAG: autofill пропущен — test_account_enabled=false")
            return
        }
        if (!matches) {
            println("$LOG_TAG: autofill пропущен — номер не совпал с test_phone_number")
            return
        }
        if (testCode.isBlank()) {
            println("$LOG_TAG: autofill пропущен — test_otp_code пустой")
            return
        }

        println("$LOG_TAG: autofill — подставляю код и автоотправляю")
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
            codeError = null
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
        if (currentState.timerSeconds > 0 || currentState.screenState is ConfirmationState.ScreenState.Submitting) return

        launchTry {
            val result =
                resendCodeUseCase(ResendRequest(currentState.phone)).toResendConfirmationResult { it.asSnackBarMessage() }

            startTimer()

            when (result) {
                is ResendConfirmationResult.Success -> setEvent {
                    ConfirmationEvent.ShowSnackbar(
                        SnackBarMessage(
                            message = UiText.Resource(Res.string.confirmation_resend_success),
                            type = SnackBarType.SUCCESS
                        )
                    )
                }

                is ResendConfirmationResult.Error -> setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
            }


        } catch {
            setEvent {
                ConfirmationEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
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
                    // TODO: временный диагностический лог — убрать после проверки.
                    println("$LOG_TAG: подтверждение успешно — переход дальше")
                    setEvent {
                        if (confirmationRequest.isRegistration) ConfirmationEvent.NavigateToLogin
                        else ConfirmationEvent.NavigateToWallet
                    }
                }
                is ConfirmationResult.Error -> {
                    // TODO: временный диагностический лог — убрать после проверки.
                    println("$LOG_TAG: подтверждение отклонено сервером — навигации не будет")
                    setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
                }
            }

            setState { copy(screenState = ConfirmationState.ScreenState.Idle) }
        } catch { error ->
            // TODO: временный диагностический лог — убрать после проверки.
            println("$LOG_TAG: ошибка запроса подтверждения: ${error::class.simpleName}: ${error.message}")
            setState { copy(screenState = ConfirmationState.ScreenState.Idle) }
            setEvent {
                ConfirmationEvent.ShowSnackbar(
                    SnackBarMessage.error(UiText.Resource(Res.string.error_unknown_error))
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        setState { copy(timerSeconds = TIMER_DURATION_SECONDS) }
        timerJob = launchTry {
            for (seconds in (TIMER_DURATION_SECONDS - 1) downTo 0) {
                delay(1000)
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