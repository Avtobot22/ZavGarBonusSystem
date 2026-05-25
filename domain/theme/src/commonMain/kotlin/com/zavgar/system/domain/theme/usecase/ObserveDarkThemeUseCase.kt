package com.zavgar.system.domain.theme.usecase

import com.zavgar.system.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ObserveDarkThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<Boolean> = themeRepository.isDarkTheme
}
