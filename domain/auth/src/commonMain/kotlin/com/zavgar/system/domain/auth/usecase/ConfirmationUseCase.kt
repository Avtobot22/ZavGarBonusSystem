package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.model.ConfirmationRequest
import com.zavgar.system.utils.result.AppResult

class ConfirmationUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(request: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        if (request.isRegistration) {
            authRepository.confirmRegistration(phone = request.phone, code = request.code)
        } else {
            authRepository.confirmLogin(phone = request.phone, code = request.code)
        }
}
