package com.zavgar.system.domain.model.response

import kotlinx.datetime.LocalDateTime

data class Transaction(
    val id: String,
    val operationType: OperationType,
    val date: LocalDateTime,
    val store: String,
    val amount: Int,
    val pointsType: PointsType,
)
