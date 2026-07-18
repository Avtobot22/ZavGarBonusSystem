package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.zavgar.system.datastore.model.CachedBalance
import kotlinx.coroutines.flow.first
import kotlin.time.Clock

internal class BalanceCacheDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : BalanceCacheDataSource {

    private companion object {
        val BALANCE_KEY = intPreferencesKey("cached_balance")
        val UPDATED_AT_KEY = longPreferencesKey("cached_balance_updated_at")
        val OWNER_KEY = stringPreferencesKey("cached_balance_owner")
    }

    override suspend fun getCachedBalance(owner: String): CachedBalance? {
        val prefs = dataStore.data.first()
        if (prefs[OWNER_KEY] != owner) return null
        val balance = prefs[BALANCE_KEY] ?: return null
        val updatedAt = prefs[UPDATED_AT_KEY] ?: return null
        return CachedBalance(balance = balance, updatedAtMillis = updatedAt)
    }

    override suspend fun saveBalance(owner: String, balance: Int) {
        dataStore.edit {
            it[OWNER_KEY] = owner
            it[BALANCE_KEY] = balance
            it[UPDATED_AT_KEY] = Clock.System.now().toEpochMilliseconds()
        }
    }

    override suspend fun clearBalance() {
        dataStore.edit {
            it.remove(OWNER_KEY)
            it.remove(BALANCE_KEY)
            it.remove(UPDATED_AT_KEY)
        }
    }
}
