package com.zavgar.system.history.mapper

import com.zavgar.system.core.presentation.util.toDayMonthYearStr
import com.zavgar.system.core.presentation.util.toHourMinuteStr
import com.zavgar.system.history.domain.model.OperationType
import com.zavgar.system.history.domain.model.PointsType
import com.zavgar.system.history.domain.model.TransactionsPageResponse
import com.zavgar.system.history.model.History
import com.zavgar.system.history.model.HistoryItem


fun TransactionsPageResponse.toPresentation(
    currentHistoryItems: List<HistoryItem> = emptyList(),
): History {

    val newHistoryItems = buildList {
        addAll(currentHistoryItems)

        var lastDateStr = currentHistoryItems
            .lastOrNull { it is HistoryItem.DateHeader }
            ?.let { (it as HistoryItem.DateHeader).date }

        this@toPresentation.transactions.forEach { transaction ->
            val dateStr = transaction.date.toDayMonthYearStr()
            val timeStr = transaction.date.toHourMinuteStr()

            if (lastDateStr != dateStr) {
                add(
                    HistoryItem.DateHeader(
                        id = "header_$dateStr",
                        date = dateStr,
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
                HistoryItem.TransactionItem(
                    id = transaction.id,
                    time = timeStr,
                    amount = amountFormatted,
                    store = transaction.store,
                    isIncome = isIncome,
                )
            )
        }
    }
    return History(
        transactions = newHistoryItems,
        nextCursor = newCursor,
        hasMore = hasMore,
    )
}
