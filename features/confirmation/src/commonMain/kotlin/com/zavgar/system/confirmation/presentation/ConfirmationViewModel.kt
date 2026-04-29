package com.zavgar.system.confirmation.presentation

import com.zavgar.system.confirmation.mapper.asSnackBarMessage
import com.zavgar.system.resources.error_unknown_error
import com.zavgar.system.confirmation.mapper.toConfirmationResult
import com.zavgar.system.confirmation.mapper.toResendConfirmationResult
import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.confirmation.domain.model.ConfirmationRequest
import com.zavgar.system.confirmation.domain.model.ResendRequest
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.SnackBarMessage
import com.zavgar.system.core.presentation.util.SnackBarType
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.confirmation.domain.usecase.ConfirmationUseCase
import com.zavgar.system.confirmation.domain.usecase.ResendCodeUseCase
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
) : BaseViewModel<ConfirmationState, ConfirmationIntent, ConfirmationEvent>(ConfirmationState()) {

    init {
        startTimer()
    }

    private var timerJob: Job? = null

    companion object {
        private const val TIMER_DURATION_SECONDS = 60
    }

    override fun handleIntent(intent: ConfirmationIntent) {
        when (intent) {
            is ConfirmationIntent.Initialize -> handleInitialize(intent.phone, intent.isRegistration)
            is ConfirmationIntent.EnterCode -> handleEnterCode(intent.code)
            is ConfirmationIntent.Submit -> handleSubmit()
            is ConfirmationIntent.ClickResend -> handleResend()
        }
    }

    private fun handleInitialize(phone: String, isRegistration: Boolean) = setState {
        copy(
            phone = phone,
            isRegistration = isRegistration
        )
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

        if (isValid is ValidationResult.Error) {
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
                is ConfirmationResult.Success -> setEvent { ConfirmationEvent.NavigateToLogin }
                is ConfirmationResult.Error -> setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
            }

            setState { copy(screenState = ConfirmationState.ScreenState.Idle) }
        } catch {
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
}