package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.zavgar.system.datastore.model.CachedBalance
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

internal class BalanceCacheDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : BalanceCacheDataSource {

    private companion object {
        val BALANCE_KEY = intPreferencesKey("cached_balance")
        val UPDATED_AT_KEY = longPreferencesKey("cached_balance_updated_at")
    }

    override suspend fun getCachedBalance(): CachedBalance? {
        val prefs = dataStore.data.first()
        val balance = prefs[BALANCE_KEY] ?: return null
        val updatedAt = prefs[UPDATED_AT_KEY] ?: return null
        return CachedBalance(balance = balance, updatedAtMillis = updatedAt)
    }

    override suspend fun saveBalance(balance: Int) {
        dataStore.edit {
            it[BALANCE_KEY] = balance
            it[UPDATED_AT_KEY] = Clock.System.now().toEpochMilliseconds()
        }
    }
}
