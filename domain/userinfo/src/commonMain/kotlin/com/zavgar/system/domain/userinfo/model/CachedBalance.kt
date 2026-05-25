package com.zavgar.system.domain.userinfo.model

/**
 * Снимок последнего успешно полученного баланса для optimistic UI.
 *
 * @param balance последнее известное значение баланса
 * @param updatedAtMillis момент последнего успешного обновления (epoch millis)
 */
data class CachedBalance(
    val balance: Int,
    val updatedAtMillis: Long,
)
