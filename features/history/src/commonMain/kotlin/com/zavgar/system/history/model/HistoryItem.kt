package com.zavgar.system.history.model

sealed interface HistoryItem {
    val id: String

    data class DateHeader(
        override val id: String,
        val date: String,
    ) : HistoryItem

    data class TransactionItem(
        override val id: String,
        val store: String,
        val time: String,
        val amount: String,
        val isIncome: Boolean,
    ) : HistoryItem
}
