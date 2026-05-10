package com.zavgar.system.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class ThemeDataSourceImpl(
    private val dataStore: DataStore<Preferences>,
) : ThemeDataSource {

    private companion object {
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
    }

    override val isDarkTheme: Flow<Boolean> =
        dataStore.data.map { it[DARK_THEME_KEY] ?: false }

    override suspend fun setDarkTheme(isDark: Boolean) {
        dataStore.edit { it[DARK_THEME_KEY] = isDark }
    }
}
