package com.zavgar.system.history.domain.model

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: Int,
    val operationType: OperationType,
    val date: LocalDateTime,
    val store: String,
    val amount: Int,
    val pointsType: PointsType,
)

enum class OperationType { CREDITING, DEBITING }

enum class PointsType { BONUS, CASHBACK }

data class TransactionsPageResponse(
    val transactions: List<Transaction>,
    val newCursor: String? = null,
    val hasMore: Boolean,
)
