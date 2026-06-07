package com.zavgar.system.domain.operations.usecase

import com.zavgar.system.domain.operations.OperationsRepository
import com.zavgar.system.domain.operations.error.OperationsError
import com.zavgar.system.domain.operations.model.OperationType
import com.zavgar.system.domain.operations.model.PointsType
import com.zavgar.system.domain.operations.model.Transaction
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.domain.operations.model.TransactionsRequest
import com.zavgar.system.utils.result.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class GetOperationsUseCaseTest {

    private val repository = mockk<OperationsRepository>()
    private val useCase = GetOperationsUseCase(repository)

    private val request = TransactionsRequest(
        periodStart = LocalDate(2026, 1, 1),
        periodEnd = LocalDate(2026, 1, 31),
        cursor = null,
    )

    @Test
    fun `passes the request through and forwards the page response`() = runTest {
        val page = TransactionsPageResponse(
            transactions = listOf(
                Transaction(
                    id = 1,
                    operationType = OperationType.CREDITING,
                    date = LocalDateTime(2026, 1, 15, 12, 0),
                    store = "Store A",
                    amount = 100,
                    pointsType = PointsType.BONUS,
                ),
            ),
            newCursor = "next",
            hasMore = true,
        )
        coEvery { repository.getOperations(request) } returns AppResult.Success(page)

        val result = useCase(request)

        assertEquals(AppResult.Success(page), result)
        coVerify(exactly = 1) { repository.getOperations(request) }
    }

    @Test
    fun `forwards a repository error unchanged`() = runTest {
        coEvery { repository.getOperations(any()) } returns
            AppResult.Error(OperationsError.NetworkError)

        val result = useCase(request)

        assertEquals(AppResult.Error(OperationsError.NetworkError), result)
    }
}
