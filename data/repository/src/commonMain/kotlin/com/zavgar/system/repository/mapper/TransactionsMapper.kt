package com.zavgar.system.repository.mapper

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import com.zavgar.system.domain.model.request.SortOrder as DomainSortOrder
import com.zavgar.system.domain.model.request.TransactionsRequest as DomainTransactionsRequest
import com.zavgar.system.domain.model.response.OperationType as DomainOperationType
import com.zavgar.system.domain.model.response.PointsType as DomainPointsType
import com.zavgar.system.domain.model.response.Transaction as DomainTransaction
import com.zavgar.system.domain.model.response.TransactionsPageResponse as DomainTransactionsPageResponse
import com.zavgar.system.repository.model.request.SortOrder as RepoSortOrder
import com.zavgar.system.repository.model.request.TransactionsRequest as RepoTransactionsRequest
import com.zavgar.system.repository.model.response.OperationType as RepoOperationType
import com.zavgar.system.repository.model.response.PointsType as RepoPointsType
import com.zavgar.system.repository.model.response.Transaction as RepoTransaction
import com.zavgar.system.repository.model.response.TransactionsPageResponse as RepoTransactionsPageResponse

fun DomainTransactionsRequest.toRepo() = RepoTransactionsRequest(
    periodStart = periodStart,
    periodEnd = periodEnd,
    cursor = cursor,
    limit = limit,
    sortOrder = sortOrder.toRepo()
)

fun DomainSortOrder.toRepo() = when (this) {
    DomainSortOrder.ASC -> RepoSortOrder.ASC
    DomainSortOrder.DESC -> RepoSortOrder.DESC
}

fun RepoTransactionsPageResponse.toDomain() = DomainTransactionsPageResponse(
    transactions = transactions.map { it.toDomain() },
    newCursor = newCursor,
    hasMore = hasMore
)

@OptIn(ExperimentalUuidApi::class)
fun RepoTransaction.toDomain() = DomainTransaction(
    id = Uuid.random().toString(),
    operationType = operationType.toDomain(),
    date = date,
    store = store,
    amount = amount,
    pointsType = pointsType.toDomain()
)

fun RepoPointsType.toDomain() = when (this) {
    RepoPointsType.CASHBACK -> DomainPointsType.CASHBACK
    RepoPointsType.BONUS -> DomainPointsType.BONUS
}

fun RepoOperationType.toDomain() = when (this) {
    RepoOperationType.CREDITING -> DomainOperationType.CREDITING
    RepoOperationType.DEBITING -> DomainOperationType.DEBITING
}