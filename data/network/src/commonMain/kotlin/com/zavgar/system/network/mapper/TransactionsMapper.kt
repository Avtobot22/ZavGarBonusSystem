package com.zavgar.system.network.mapper

import com.zavgar.system.network.model.OperationType as NetworkOperationType
import com.zavgar.system.network.model.PointsType as NetworkPointsType
import com.zavgar.system.network.model.SortOrder as NetworkSortOrder
import com.zavgar.system.network.model.Transaction as NetworkTransaction
import com.zavgar.system.network.model.TransactionsPageResponse as NetworkTransactionsPageResponse
import com.zavgar.system.network.model.TransactionsRequest as NetworkTransactionsRequest
import com.zavgar.system.repository.model.request.SortOrder as RepoSortOrder
import com.zavgar.system.repository.model.request.TransactionsRequest as RepoTransactionsRequest
import com.zavgar.system.repository.model.response.OperationType as RepoOperationType
import com.zavgar.system.repository.model.response.PointsType as RepoPointsType
import com.zavgar.system.repository.model.response.Transaction as RepoTransaction
import com.zavgar.system.repository.model.response.TransactionsPageResponse as RepoTransactionsPageResponse

fun RepoTransactionsRequest.toNetwork() = NetworkTransactionsRequest(
    periodStart = periodStart,
    periodEnd = periodEnd,
    cursor = cursor,
    limit = limit,
    sortOrder = sortOrder.toNetwork()
)

fun RepoSortOrder.toNetwork() = when (this) {
    RepoSortOrder.ASC -> NetworkSortOrder.ASC
    RepoSortOrder.DESC -> NetworkSortOrder.DESC
}

fun NetworkTransactionsPageResponse.toRepo() = RepoTransactionsPageResponse(
    transactions = items.map { it.toRepo() },
    newCursor = nextCursor,
    hasMore = hasMore
)

fun NetworkTransaction.toRepo() = RepoTransaction(
    operationType = operationType.toRepo(),
    date = date,
    store = store,
    amount = amount,
    pointsType = pointsType.toRepo()
)

fun NetworkPointsType.toRepo() = when (this) {
    NetworkPointsType.CASHBACK -> RepoPointsType.CASHBACK
    NetworkPointsType.BONUS -> RepoPointsType.BONUS
}

fun NetworkOperationType.toRepo() = when (this) {
    NetworkOperationType.CREDITING -> RepoOperationType.CREDITING
    NetworkOperationType.DEBITING -> RepoOperationType.DEBITING
}