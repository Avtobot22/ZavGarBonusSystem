package com.zavgar.system.domain.theme.usecase

import com.zavgar.system.domain.theme.repository.ThemeRepository

class SetDarkThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(isDark: Boolean) = themeRepository.setDarkTheme(isDark)
}
