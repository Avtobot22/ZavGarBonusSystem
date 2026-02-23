package com.zavgar.system.confirmation.presentation

import androidx.lifecycle.viewModelScope
import com.zavgar.system.confirmation.mapper.asUiText
import com.zavgar.system.confirmation.mapper.toConfirmationResult
import com.zavgar.system.confirmation.mapper.toDomain
import com.zavgar.system.confirmation.mapper.toResendConfirmationResult
import com.zavgar.system.confirmation.model.ConfirmationRequest
import com.zavgar.system.confirmation.model.ConfirmationResult
import com.zavgar.system.confirmation.model.ResendConfirmationResult
import com.zavgar.system.confirmation.model.ResendRequest
import com.zavgar.system.core.presentation.BaseViewModel
import com.zavgar.system.core.presentation.util.UiText
import com.zavgar.system.domain.usecase.ConfirmationUseCase
import com.zavgar.system.domain.usecase.ResendCodeUseCase
import com.zavgar.system.domain.usecase.validation.ValidateCodeUseCase
import com.zavgar.system.resources.Res
import com.zavgar.system.resources.confirmation_resend_success
import com.zavgar.system.sharedValidation.ValidationResult
import com.zavgar.system.sharedValidation.asUiText
import com.zavgar.system.sharedValidation.toPresentation
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        if (currentState.isLoading) return
        val isValid = validateCodeUseCase(currentState.code).toPresentation { it.asUiText() }

        if (isValid is ValidationResult.Error) {
            setState { copy(codeError = isValid.error) }
            return
        }

        performConfirmation(ConfirmationRequest(currentState.phone, currentState.code, currentState.isRegistration))

    }

    private fun handleResend() {
        if (currentState.timerSeconds > 0 || currentState.isLoading) return

        viewModelScope.launch {
            val result =
                resendCodeUseCase(ResendRequest(currentState.phone).toDomain()).toResendConfirmationResult { it.asUiText() }

            startTimer()

            when (result) {
                is ResendConfirmationResult.Success -> setEvent {
                    ConfirmationEvent.ShowSnackbar(UiText.Resource(Res.string.confirmation_resend_success))
                }

                is ResendConfirmationResult.Error -> setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
            }


        }
    }

    private fun performConfirmation(confirmationRequest: ConfirmationRequest) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            val result = confirmationUseCase(confirmationRequest.toDomain()).toConfirmationResult { it.asUiText() }

            when (result) {
                is ConfirmationResult.Success -> setEvent { ConfirmationEvent.NavigateToLogin }
                is ConfirmationResult.Error -> setEvent { ConfirmationEvent.ShowSnackbar(result.message) }
            }

            setState { copy(isLoading = false) }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        setState { copy(timerSeconds = TIMER_DURATION_SECONDS) }
        timerJob = viewModelScope.launch {
            for (seconds in (TIMER_DURATION_SECONDS - 1) downTo 0) {
                delay(1000)
                setState { copy(timerSeconds = seconds) }
            }
        }
    }
}