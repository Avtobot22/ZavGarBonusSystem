package com.zavgar.system.network.remote

import com.zavgar.system.network.model.ConfirmationRequest
import com.zavgar.system.network.model.LoginRequest
import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.model.RegisterRequest
import com.zavgar.system.network.model.ResendRequest

interface AuthService {
    suspend fun loginRequest(loginRequest: LoginRequest): Result<Unit>

    suspend fun registerRequest(registerRequest: RegisterRequest): Result<Unit>

    suspend fun confirmLogin(confirmationRequest: ConfirmationRequest): Result<LoginResponse>

    suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest): Result<LoginResponse>

    suspend fun resendCode(resendRequest: ResendRequest): Result<Unit>
}
