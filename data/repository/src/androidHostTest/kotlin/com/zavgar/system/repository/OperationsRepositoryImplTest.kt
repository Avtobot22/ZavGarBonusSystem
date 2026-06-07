package com.zavgar.system.repository

import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.OperationType
import com.zavgar.system.domain.operations.model.PointsType
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.network.remote.LoyaltyService
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import com.zavgar.system.network.model.OperationType as NetworkOperationType
import com.zavgar.system.network.model.PointsType as NetworkPointsType
import com.zavgar.system.network.model.Transaction as NetworkTransaction
import com.zavgar.system.network.model.TransactionsPageResponse as NetworkPage

class OperationsRepositoryImplTest {

    private val loyaltyService = mockk<LoyaltyService>()
    private val repository = OperationsRepositoryImpl(loyaltyService, TestDispatcherProvider())

    private val request = TransactionsRequest(
        periodStart = LocalDate(2026, 1, 1),
        periodEnd = LocalDate(2026, 1, 31),
        cursor = null,
    )

    @Test
    fun `getOperations maps the network page and its enums to the domain model`() = runTest {
        val networkPage = NetworkPage(
            items = listOf(
                NetworkTransaction(
                    id = 1,
                    operationType = NetworkOperationType.CREDITING,
                    date = LocalDateTime(2026, 1, 10, 8, 30),
                    store = "Store",
                    amount = 100,
                    pointsType = NetworkPointsType.CASHBACK,
                    phone = "1234567890",
                ),
            ),
            nextCursor = "next",
            hasMore = true,
        )
        coEvery { loyaltyService.getOperations(any()) } returns Result.success(networkPage)

        val result = repository.getOperations(request)

        assertTrue(result is AppResult.Success)
        val page = result.data
        assertEquals("next", page.newCursor)
        assertTrue(page.hasMore)
        val transaction = page.transactions.single()
        assertEquals(1, transaction.id)
        assertEquals(OperationType.CREDITING, transaction.operationType)
        assertEquals(PointsType.CASHBACK, transaction.pointsType)
        assertEquals("Store", transaction.store)
        assertEquals(100, transaction.amount)
    }

    @Test
    fun `getOperations maps a 400 to ValidationError`() = runTest {
        coEvery { loyaltyService.getOperations(any()) } returns Result.failure(clientError(400))

        assertEquals(AppResult.Error(OperationsError.ValidationError), repository.getOperations(request))
    }

    @Test
    fun `getOperations maps a network failure`() = runTest {
        coEvery { loyaltyService.getOperations(any()) } returns Result.failure(networkError())

        assertEquals(AppResult.Error(OperationsError.NetworkError), repository.getOperations(request))
    }
}
