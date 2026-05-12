package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.model.ConfirmationRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class ConfirmationUseCase(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            if (request.isRegistration) {
                authRepository.confirmRegistration(phone = request.phone, code = request.code)
            } else {
                authRepository.confirmReset(phone = request.phone, code = request.code)
            }
        }
}
