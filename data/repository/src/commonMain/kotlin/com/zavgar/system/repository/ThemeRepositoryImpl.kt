package com.zavgar.system.repository

import com.zavgar.system.datastore.datasource.ThemeDataSource
import com.zavgar.system.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val dataSource: ThemeDataSource,
) : ThemeRepository {

    override val isDarkTheme: Flow<Boolean> = dataSource.isDarkTheme

    override suspend fun setDarkTheme(isDark: Boolean) = dataSource.setDarkTheme(isDark)
}
