package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.BalanceCacheDataSource
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.datastore.model.Session
import com.zavgar.system.domain.auth.error.AuthError
import com.zavgar.system.domain.auth.error.ConfirmationError
import com.zavgar.system.domain.auth.error.RegisterError
import com.zavgar.system.domain.auth.error.ResendConfirmationError
import com.zavgar.system.network.auth.AuthTokenCache
import com.zavgar.system.network.model.LoginResponse
import com.zavgar.system.network.remote.AuthService
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AuthRepositoryImplTest {

    private val authService = mockk<AuthService>()
    private val sessionDataSource = mockk<SessionDataSource>()
    private val balanceCacheDataSource = mockk<BalanceCacheDataSource>(relaxed = true)
    private val authTokenCache = mockk<AuthTokenCache>(relaxed = true)
    private val repository = AuthRepositoryImpl(
        authService,
        sessionDataSource,
        balanceCacheDataSource,
        authTokenCache,
        TestDispatcherProvider(),
    )

    @Test
    fun `login returns Success when the service succeeds`() = runTest {
        coEvery { authService.loginRequest(any()) } returns Result.success(Unit)

        assertEquals(AppResult.Success(Unit), repository.login("1234567890"))
    }

    @Test
    fun `login maps client status codes to auth errors`() = runTest {
        coEvery { authService.loginRequest(any()) } returns Result.failure(clientError(400))
        assertEquals(AppResult.Error(AuthError.ValidationError), repository.login("x"))

        coEvery { authService.loginRequest(any()) } returns Result.failure(clientError(404))
        assertEquals(AppResult.Error(AuthError.UserNotFound), repository.login("x"))

        coEvery { authService.loginRequest(any()) } returns Result.failure(clientError(429))
        assertEquals(AppResult.Error(AuthError.TooManyRequestError()), repository.login("x"))

        coEvery { authService.loginRequest(any()) } returns Result.failure(clientError(418, "teapot"))
        assertEquals(AppResult.Error(AuthError.UnknownError("teapot")), repository.login("x"))
    }

    @Test
    fun `login maps server and network failures`() = runTest {
        coEvery { authService.loginRequest(any()) } returns Result.failure(serverError())
        assertEquals(AppResult.Error(AuthError.ServerError), repository.login("x"))

        coEvery { authService.loginRequest(any()) } returns Result.failure(networkError())
        assertEquals(AppResult.Error(AuthError.NetworkError), repository.login("x"))
    }

    @Test
    fun `register maps a 409 conflict to UserAlreadyExists`() = runTest {
        coEvery { authService.registerRequest(any()) } returns Result.failure(clientError(409))

        val result = repository.register("John", kotlinx.datetime.LocalDate(1990, 1, 1), "1234567890")

        assertEquals(AppResult.Error(RegisterError.UserAlreadyExists), result)
    }

    @Test
    fun `register returns Success when the service succeeds`() = runTest {
        coEvery { authService.registerRequest(any()) } returns Result.success(Unit)

        val result = repository.register("John", kotlinx.datetime.LocalDate(1990, 1, 1), "1234567890")

        assertEquals(AppResult.Success(Unit), result)
    }

    @Test
    fun `confirmLogin saves the session and returns Success`() = runTest {
        coEvery { authService.confirmLogin(any()) } returns Result.success(
            LoginResponse(accessToken = "access", accessExpiresIn = 1, refreshToken = "refresh", refreshExpiresIn = 2),
        )
        coEvery { sessionDataSource.saveSession(any()) } returns Result.success(Unit)

        val result = repository.confirmLogin(phone = "1234567890", code = "1111")

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) {
            sessionDataSource.saveSession(
                Session(accessToken = "access", refreshToken = "refresh", phone = "1234567890"),
            )
        }
        coVerify(exactly = 1) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `confirmLogin returns UnknownError when saving the session fails`() = runTest {
        coEvery { authService.confirmLogin(any()) } returns Result.success(
            LoginResponse(accessToken = "a", accessExpiresIn = 1, refreshToken = "r", refreshExpiresIn = 2),
        )
        coEvery { sessionDataSource.saveSession(any()) } returns Result.failure(RuntimeException("disk full"))

        val result = repository.confirmLogin(phone = "1234567890", code = "1111")

        assertEquals(AppResult.Error(ConfirmationError.UnknownError("disk full")), result)
        verify(exactly = 0) { authTokenCache.clear() }
    }

    @Test
    fun `confirmLogin maps a service failure and never touches the session`() = runTest {
        coEvery { authService.confirmLogin(any()) } returns Result.failure(clientError(400))

        val result = repository.confirmLogin(phone = "1234567890", code = "0000")

        assertEquals(AppResult.Error(ConfirmationError.InvalidCodeError), result)
        coVerify(exactly = 0) { sessionDataSource.saveSession(any()) }
        coVerify(exactly = 0) { balanceCacheDataSource.clearBalance() }
        verify(exactly = 0) { authTokenCache.clear() }
    }

    @Test
    fun `confirmRegistration saves the session and returns Success`() = runTest {
        coEvery { authService.confirmRegistration(any()) } returns Result.success(
            LoginResponse(accessToken = "access", accessExpiresIn = 1, refreshToken = "refresh", refreshExpiresIn = 2),
        )
        coEvery { sessionDataSource.saveSession(any()) } returns Result.success(Unit)

        val result = repository.confirmRegistration(phone = "1234567890", code = "1111")

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) {
            sessionDataSource.saveSession(
                Session(accessToken = "access", refreshToken = "refresh", phone = "1234567890"),
            )
        }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `confirmRegistration forwards a mapped error and never touches the session`() = runTest {
        coEvery { authService.confirmRegistration(any()) } returns Result.failure(clientError(429))

        val result = repository.confirmRegistration(phone = "1234567890", code = "1111")

        assertEquals(AppResult.Error(ConfirmationError.TooManyRequestError()), result)
        coVerify(exactly = 0) { sessionDataSource.saveSession(any()) }
    }

    @Test
    fun `confirmLogin still saves an isolated session when old balance cleanup fails`() = runTest {
        coEvery { authService.confirmLogin(any()) } returns Result.success(
            LoginResponse(accessToken = "a", accessExpiresIn = 1, refreshToken = "r", refreshExpiresIn = 2),
        )
        coEvery { balanceCacheDataSource.clearBalance() } throws RuntimeException("cache")
        coEvery { sessionDataSource.saveSession(any()) } returns Result.success(Unit)

        val result = repository.confirmLogin(phone = "1234567890", code = "1111")

        assertEquals(AppResult.Success(Unit), result)
        coVerify(exactly = 1) { sessionDataSource.saveSession(any()) }
        verify(exactly = 1) { authTokenCache.clear() }
    }

    @Test
    fun `resendCode maps a 400 to InvalidPhone`() = runTest {
        coEvery { authService.resendCode(any()) } returns Result.failure(clientError(400))

        val result = repository.resendCode("1234567890")

        assertEquals(AppResult.Error(ResendConfirmationError.InvalidPhone), result)
    }
}
