package com.zavgar.system.history.mapper

import com.zavgar.system.domain.operations.model.OperationType
import com.zavgar.system.domain.operations.model.PointsType
import com.zavgar.system.domain.operations.model.Transaction
import com.zavgar.system.domain.operations.model.TransactionsPageResponse
import com.zavgar.system.history.model.HistoryItem
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransactionsMapperTest {

    private fun transaction(
        id: Int,
        date: LocalDateTime,
        operationType: OperationType = OperationType.CREDITING,
        store: String = "Store",
        amount: Int = 100,
        pointsType: PointsType = PointsType.BONUS,
    ) = Transaction(id, operationType, date, store, amount, pointsType)

    @Test
    fun `groups transactions of the same day under a single date header`() {
        val page = TransactionsPageResponse(
            transactions = listOf(
                transaction(1, LocalDateTime(2026, 1, 15, 10, 30)),
                transaction(2, LocalDateTime(2026, 1, 15, 18, 5)),
            ),
            newCursor = "cursor",
            hasMore = false,
        )

        val history = page.toPresentation()

        assertEquals(3, history.transactions.size)
        val header = history.transactions[0] as HistoryItem.DateHeader
        assertEquals("15.01.2026", header.date)
        assertEquals("header_15.01.2026", header.id)
        assertTrue(history.transactions[1] is HistoryItem.TransactionItem)
        assertTrue(history.transactions[2] is HistoryItem.TransactionItem)
        assertEquals("cursor", history.nextCursor)
        assertEquals(false, history.hasMore)
    }

    @Test
    fun `creates a new header for each distinct day`() {
        val page = TransactionsPageResponse(
            transactions = listOf(
                transaction(1, LocalDateTime(2026, 1, 15, 10, 0)),
                transaction(2, LocalDateTime(2026, 1, 16, 10, 0)),
            ),
            newCursor = null,
            hasMore = true,
        )

        val history = page.toPresentation()

        val headers = history.transactions.filterIsInstance<HistoryItem.DateHeader>()
        assertEquals(listOf("15.01.2026", "16.01.2026"), headers.map { it.date })
        assertEquals(4, history.transactions.size)
    }

    @Test
    fun `formats a crediting bonus as a positive amount marked as income`() {
        val page = TransactionsPageResponse(
            transactions = listOf(
                transaction(
                    id = 7,
                    date = LocalDateTime(2026, 3, 1, 9, 5),
                    operationType = OperationType.CREDITING,
                    store = "Coffee",
                    amount = 250,
                    pointsType = PointsType.BONUS,
                ),
            ),
            newCursor = null,
            hasMore = false,
        )

        val item = page.toPresentation().transactions
            .filterIsInstance<HistoryItem.TransactionItem>()
            .single()

        assertEquals("7", item.id)
        assertEquals("09:05", item.time)
        assertEquals("+250 б.", item.amount)
        assertEquals("Coffee", item.store)
        assertTrue(item.isIncome)
    }

    @Test
    fun `formats a debiting cashback as a negative amount not marked as income`() {
        val page = TransactionsPageResponse(
            transactions = listOf(
                transaction(
                    id = 8,
                    date = LocalDateTime(2026, 3, 1, 23, 59),
                    operationType = OperationType.DEBITING,
                    amount = 75,
                    pointsType = PointsType.CASHBACK,
                ),
            ),
            newCursor = null,
            hasMore = false,
        )

        val item = page.toPresentation().transactions
            .filterIsInstance<HistoryItem.TransactionItem>()
            .single()

        assertEquals("-75 ₽", item.amount)
        assertEquals("23:59", item.time)
        assertTrue(!item.isIncome)
    }

    @Test
    fun `appends to existing items without repeating a header for the same trailing day`() {
        val existing = listOf(
            HistoryItem.DateHeader(id = "header_15.01.2026", date = "15.01.2026"),
            HistoryItem.TransactionItem(id = "1", store = "S", time = "10:00", amount = "+1 б.", isIncome = true),
        )
        val page = TransactionsPageResponse(
            transactions = listOf(transaction(2, LocalDateTime(2026, 1, 15, 11, 0))),
            newCursor = null,
            hasMore = false,
        )

        val history = page.toPresentation(currentHistoryItems = existing)

        // No second "15.01.2026" header is added; the new transaction is appended.
        assertEquals(1, history.transactions.count { it is HistoryItem.DateHeader })
        assertEquals(3, history.transactions.size)
        assertEquals("2", (history.transactions.last() as HistoryItem.TransactionItem).id)
    }

    @Test
    fun `maps an empty page to an empty history preserving paging fields`() {
        val page = TransactionsPageResponse(transactions = emptyList(), newCursor = "c", hasMore = true)

        val history = page.toPresentation()

        assertTrue(history.transactions.isEmpty())
        assertEquals("c", history.nextCursor)
        assertEquals(true, history.hasMore)
    }
}
