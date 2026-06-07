package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.BalanceCacheDataSource
import com.zavgar.system.datastore.datasource.SessionDataSource
import com.zavgar.system.domain.userinfo.error.DeleteError
import com.zavgar.system.domain.userinfo.error.GetBalanceError
import com.zavgar.system.domain.userinfo.error.LogoutError
import com.zavgar.system.domain.userinfo.error.MonthlyAccrualsError
import com.zavgar.system.domain.userinfo.error.ProfileError
import com.zavgar.system.domain.userinfo.model.Balance
import com.zavgar.system.domain.userinfo.model.CachedBalance
import com.zavgar.system.domain.userinfo.model.UserProfile
import com.zavgar.system.network.model.AccrualsSumResponse
import com.zavgar.system.network.model.BalanceResponse
import com.zavgar.system.network.model.ProfileResponse
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.network.remote.UserProfileService
import com.zavgar.system.utils.result.AppResult
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import com.zavgar.system.datastore.model.CachedBalance as StoredBalance

class ProfileRepositoryImplTest {

    private val userProfileService = mockk<UserProfileService>()
    private val loyaltyService = mockk<LoyaltyService>()
    private val sessionDataSource = mockk<SessionDataSource>()
    private val balanceCacheDataSource = mockk<BalanceCacheDataSource>()
    private val repository = ProfileRepositoryImpl(
        userProfileService,
        loyaltyService,
        sessionDataSource,
        balanceCacheDataSource,
        TestDispatcherProvider(),
    )

    @Test
    fun `getProfile maps the network response to a domain profile`() = runTest {
        val birthDate = LocalDate(1990, 1, 1)
        coEvery { userProfileService.getProfile() } returns
            Result.success(ProfileResponse(name = "John", phone = "1234567890", birthDate = birthDate))

        val result = repository.getProfile()

        assertEquals(
            AppResult.Success(UserProfile(name = "John", phone = "1234567890", birthDate = birthDate)),
            result,
        )
    }

    @Test
    fun `getProfile maps a 404 to UserNotFound`() = runTest {
        coEvery { userProfileService.getProfile() } returns Result.failure(clientError(404))

        assertEquals(AppResult.Error(ProfileError.UserNotFound), repository.getProfile())
    }

    @Test
    fun `updateProfile returns Success when the service succeeds`() = runTest {
        coEvery { userProfileService.updateProfile(any()) } returns Result.success(Unit)

        val result = repository.updateProfile("Jane", LocalDate(2000, 2, 2))

        assertEquals(AppResult.Success(Unit), result)
    }

    @Test
    fun `getBalance caches the value and returns it on success`() = runTest {
        coEvery { loyaltyService.getBalance() } returns Result.success(BalanceResponse(balance = 750))
        coEvery { balanceCacheDataSource.saveBalance(any()) } just Runs

        val result = repository.getBalance()

        assertEquals(AppResult.Success(Balance(750)), result)
        coVerify(exactly = 1) { balanceCacheDataSource.saveBalance(750) }
    }

    @Test
    fun `getBalance maps a 429 to TooManyRequest and does not cache`() = runTest {
        coEvery { loyaltyService.getBalance() } returns Result.failure(clientError(429))

        val result = repository.getBalance()

        assertEquals(AppResult.Error(GetBalanceError.TooManyRequestError), result)
        coVerify(exactly = 0) { balanceCacheDataSource.saveBalance(any()) }
    }

    @Test
    fun `getCachedBalance maps a stored balance to the domain model`() = runTest {
        coEvery { balanceCacheDataSource.getCachedBalance() } returns
            StoredBalance(balance = 320, updatedAtMillis = 1_700_000_000_000)

        assertEquals(CachedBalance(balance = 320, updatedAtMillis = 1_700_000_000_000), repository.getCachedBalance())
    }

    @Test
    fun `getCachedBalance returns null when nothing is stored`() = runTest {
        coEvery { balanceCacheDataSource.getCachedBalance() } returns null

        assertNull(repository.getCachedBalance())
    }

    @Test
    fun `getMonthlyAccruals forwards the sum`() = runTest {
        coEvery { loyaltyService.getAccrualsSum(any()) } returns Result.success(AccrualsSumResponse(sum = 99))

        assertEquals(AppResult.Success(99), repository.getMonthlyAccruals())
    }

    @Test
    fun `getMonthlyAccruals maps a server failure`() = runTest {
        coEvery { loyaltyService.getAccrualsSum(any()) } returns Result.failure(serverError())

        assertEquals(AppResult.Error(MonthlyAccrualsError.ServerError), repository.getMonthlyAccruals())
    }

    @Test
    fun `delete returns Success when both the server and the session are cleared`() = runTest {
        coEvery { userProfileService.delete() } returns Result.success(Unit)
        coEvery { sessionDataSource.deleteSession() } returns Result.success(Unit)

        assertEquals(AppResult.Success(Unit), repository.delete())
    }

    @Test
    fun `delete maps a server failure before clearing the session`() = runTest {
        coEvery { userProfileService.delete() } returns Result.failure(serverError())
        coEvery { sessionDataSource.deleteSession() } returns Result.success(Unit)

        assertEquals(AppResult.Error(DeleteError.ServerError), repository.delete())
    }

    @Test
    fun `delete reports an UnknownError when only the local cleanup fails`() = runTest {
        coEvery { userProfileService.delete() } returns Result.success(Unit)
        coEvery { sessionDataSource.deleteSession() } returns Result.failure(RuntimeException("cleanup"))

        assertEquals(AppResult.Error(DeleteError.UnknownError("cleanup")), repository.delete())
    }

    @Test
    fun `logout returns Success when both calls succeed`() = runTest {
        coEvery { userProfileService.logout() } returns Result.success(Unit)
        coEvery { sessionDataSource.deleteSession() } returns Result.success(Unit)

        assertEquals(AppResult.Success(Unit), repository.logout())
    }

    @Test
    fun `logout maps a server failure`() = runTest {
        coEvery { userProfileService.logout() } returns Result.failure(clientError(429))
        coEvery { sessionDataSource.deleteSession() } returns Result.success(Unit)

        assertEquals(AppResult.Error(LogoutError.TooManyRequestError), repository.logout())
    }

    @Test
    fun `logout reports an UnknownError when local session deletion fails`() = runTest {
        coEvery { userProfileService.logout() } returns Result.success(Unit)
        coEvery { sessionDataSource.deleteSession() } returns Result.failure(RuntimeException("local"))

        assertEquals(AppResult.Error(LogoutError.UnknownError("local")), repository.logout())
    }
}
