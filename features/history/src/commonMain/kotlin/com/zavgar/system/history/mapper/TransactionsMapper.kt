package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.toDayMonthYearStr
import com.zavgar.system.core.presentation.util.toHourMinuteStr
import com.zavgar.system.domain.model.response.OperationType
import com.zavgar.system.domain.model.response.PointsType
import com.zavgar.system.domain.model.request.TransactionsRequest as DomainTransactionsRequest
import com.zavgar.system.domain.model.response.TransactionsPageResponse as DomainTransactionsPageResponse
import com.zavgar.system.history.model.History as UiHistory
import com.zavgar.system.history.model.HistoryItem as UiHistoryItem
import com.zavgar.system.history.model.TransactionsRequest as UiTransactionsRequest


fun UiTransactionsRequest.toDomain() = DomainTransactionsRequest(
    periodStart = periodStart,
    periodEnd = periodEnd,
    cursor = cursor,
    limit = limit
)

fun DomainTransactionsPageResponse.toPresentation(
    currentHistoryItems: List<UiHistoryItem> = emptyList()
): UiHistory {

    val newHistoryItems = buildList {
        addAll(currentHistoryItems)

        var lastDateStr = currentHistoryItems
            .lastOrNull { it is UiHistoryItem.DateHeader }
            ?.let { (it as UiHistoryItem.DateHeader).date }

        this@toPresentation.transactions.forEach { transaction ->
            val dateStr = transaction.date.toDayMonthYearStr()
            val timeStr = transaction.date.toHourMinuteStr()

            if (lastDateStr != dateStr) {
                add(
                    UiHistoryItem.DateHeader(
                        id = "header_$dateStr",
                        date = dateStr
                    )
                )
                lastDateStr = dateStr
            }

            val isIncome = transaction.operationType == OperationType.CREDITING
            val sign = if (isIncome) "+" else "-"

            val pointsSymbol = when (transaction.pointsType) {
                PointsType.BONUS -> "б."
                PointsType.CASHBACK -> "₽"
            }

            val amountFormatted = "$sign${transaction.amount} $pointsSymbol"

            add(
                UiHistoryItem.TransactionItem(
                    id = transaction.id,
                    time = timeStr,
                    amount = amountFormatted,
                    store = transaction.store,
                    isIncome = isIncome
                )
            )
        }
    }
    return UiHistory(
        transactions = newHistoryItems,
        nextCursor = newCursor,
        hasMore = hasMore
    )
}