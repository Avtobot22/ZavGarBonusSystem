package com.zavgar.system.repository.model.response

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val operationType: OperationType,
    val date: LocalDateTime,
    val store: String,
    val amount: Int,
    val pointsType: PointsType,
)
