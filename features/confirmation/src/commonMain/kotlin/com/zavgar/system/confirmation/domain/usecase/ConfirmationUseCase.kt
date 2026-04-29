package com.zavgar.system.confirmation.domain.usecase

import com.zavgar.system.confirmation.domain.error.ConfirmationError
import com.zavgar.system.confirmation.domain.error.toConfirmationError
import com.zavgar.system.confirmation.domain.model.ConfirmationRequest
import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.ConfirmationRequest as NetworkConfirmationRequest

class ConfirmationUseCase(
    private val authService: AuthService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(confirmationRequest: ConfirmationRequest): AppResult<Unit, ConfirmationError> =
        withContext(dispatcherProvider.io) {
            val networkRequest = NetworkConfirmationRequest(
                phone = confirmationRequest.phone,
                code = confirmationRequest.code,
            )
            val result = if (confirmationRequest.isRegistration) {
                authService.confirmRegistration(networkRequest)
            } else {
                authService.confirmReset(networkRequest)
            }
            result.toAppResult(Throwable::toConfirmationError)
        }
}
