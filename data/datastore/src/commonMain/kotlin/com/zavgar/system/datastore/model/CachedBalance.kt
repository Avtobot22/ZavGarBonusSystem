package com.zavgar.system.datastore.model

/**
 * Локальный снимок последнего успешно полученного баланса.
 *
 * @param balance последнее известное значение баланса
 * @param updatedAtMillis момент сохранения (epoch millis)
 */
data class CachedBalance(
    val balance: Int,
    val updatedAtMillis: Long,
)
