package com.zavgar.system.network.remote

import com.zavgar.system.coroutines.runSuspendCatching
import com.zavgar.system.network.mapper.toNetwork
import com.zavgar.system.network.mapper.toRepo
import com.zavgar.system.repository.model.request.ConfirmationRequest
import com.zavgar.system.repository.model.request.LoginRequest
import com.zavgar.system.repository.model.request.RegisterRequest
import com.zavgar.system.repository.model.request.ResendRequest
import com.zavgar.system.repository.model.request.ResetPasswordRequest
import com.zavgar.system.repository.remote.AuthService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.path
import com.zavgar.system.network.model.LoginResponse as NetworkLoginResponse


class AuthServiceImpl(
    private val client: HttpClient
) : AuthService {
    override suspend fun loginRequest(loginRequest: LoginRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/login")
            }

            setBody(loginRequest.toNetwork())
        }

        response.body<NetworkLoginResponse>().toRepo()
    }

    override suspend fun registerRequest(registerRequest: RegisterRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/register")
            }

            setBody(registerRequest.toNetwork())
        }

        response.body<Unit>()
    }

    override suspend fun confirmRegistration(confirmationRequest: ConfirmationRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/confirm/register")
            }

            setBody(confirmationRequest.toNetwork())
        }

        response.body<Unit>()
    }

    override suspend fun confirmReset(confirmationRequest: ConfirmationRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/confirm/reset")
            }

            setBody(confirmationRequest.toNetwork())
        }

        response.body<Unit>()
    }

    override suspend fun resendCode(resendRequest: ResendRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/refresh/code")
            }

            setBody(resendRequest.toNetwork())
        }

        response.body<Unit>()
    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest) = runSuspendCatching {
        val response = client.post {
            url {
                path("auth/reset")
            }

            setBody(resetPasswordRequest.toNetwork())
        }

        response.body<Unit>()
    }
}