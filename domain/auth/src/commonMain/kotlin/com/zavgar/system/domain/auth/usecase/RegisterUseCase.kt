package com.zavgar.system.domain.auth.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.domain.auth.AuthRepository
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.model.RegisterRequest
import com.zavgar.system.utils.result.AppResult
import kotlinx.coroutines.withContext

class RegisterUseCase(
    private val authRepository: AuthRepository,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(request: RegisterRequest): AppResult<Unit, RegisterError> =
        withContext(dispatcherProvider.io) {
            authRepository.register(
                name = request.name,
                birthDate = request.birthDate,
                phone = request.phone,
                password = request.password,
            )
        }
}
