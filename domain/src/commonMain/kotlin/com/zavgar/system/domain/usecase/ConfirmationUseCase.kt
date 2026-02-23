package com.zavgar.system.domain.usecase

import com.zavgar.system.domain.model.AppResult
import com.zavgar.system.domain.model.error.ConfirmationError
import com.zavgar.system.domain.model.request.ConfirmationRequest
import com.zavgar.system.domain.repository.AuthRepository

class ConfirmationUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> {
        return if (confirmationRequest.isRegistration) {
            authRepository.confirmRegistration(confirmationRequest)
        } else {
            authRepository.confirmReset(confirmationRequest)
        }
    }
}