package com.zavgar.system.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface ThemeDataSource {

    val isDarkTheme: Flow<Boolean>

    suspend fun setDarkTheme(isDark: Boolean)
}
