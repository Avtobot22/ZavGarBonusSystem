package com.zavgar.system.registration.domain.usecase

import com.zavgar.system.coroutines.CoroutineDispatcherProvider
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.registration.domain.error.RegisterError
import com.zavgar.system.registration.domain.error.toRegisterError
import com.zavgar.system.registration.domain.model.RegisterRequest
import com.zavgar.system.utils.result.AppResult
import com.zavgar.system.utils.result.toAppResult
import kotlinx.coroutines.withContext
import com.zavgar.system.network.model.RegisterRequest as NetworkRegisterRequest

class RegisterUseCase(
    private val authService: AuthService,
    private val dispatcherProvider: CoroutineDispatcherProvider,
) {
    suspend operator fun invoke(registerRequest: RegisterRequest): AppResult<Unit, RegisterError> =
        withContext(dispatcherProvider.io) {
            authService.registerRequest(
                NetworkRegisterRequest(
                    name = registerRequest.name,
                    birthDate = registerRequest.birthDate,
                    phone = registerRequest.phone,
                    password = registerRequest.password,
                )
            ).toAppResult(Throwable::toRegisterError)
        }
}
