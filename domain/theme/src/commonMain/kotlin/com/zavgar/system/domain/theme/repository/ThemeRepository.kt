package com.zavgar.system.domain.theme.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {

    val isDarkTheme: Flow<Boolean>

    suspend fun setDarkTheme(isDark: Boolean)
}
