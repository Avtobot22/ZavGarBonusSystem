package com.zavgar.system.domain.auth

import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.utils.result.AppResult
import kotlinx.datetime.LocalDate

interface AuthRepository {
    suspend fun login(phone: String): AppResult<Unit, AuthError>
    suspend fun register(name: String, birthDate: LocalDate, phone: String): AppResult<Unit, RegisterError>
    suspend fun confirmLogin(phone: String, code: String): AppResult<Unit, ConfirmationError>
    suspend fun confirmRegistration(phone: String, code: String): AppResult<Unit, ConfirmationError>
    suspend fun resendCode(phone: String): AppResult<Unit, ResendConfirmationError>
}
