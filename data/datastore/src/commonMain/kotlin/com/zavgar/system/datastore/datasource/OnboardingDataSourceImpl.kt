package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class OnboardingDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : OnboardingDataSource {

    private companion object {
        val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    }

    override val isOnboardingCompleted: Flow<Boolean> =
        dataStore.data.map { it[ONBOARDING_COMPLETED_KEY] ?: false }

    override suspend fun setOnboardingCompleted() {
        dataStore.edit { it[ONBOARDING_COMPLETED_KEY] = true }
    }
}
