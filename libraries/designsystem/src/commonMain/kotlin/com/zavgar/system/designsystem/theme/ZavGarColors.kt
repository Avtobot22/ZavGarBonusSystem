package com.zavgar.system.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Расширенный набор токенов цвета, соответствующих CSS-переменным прототипа
 * `ZavGar App.html`. Material3 [androidx.compose.material3.ColorScheme] не покрывает
 * полупрозрачные оттенки текста (fg2/fg3), мягкий акцент и кастомные тени, поэтому
 * все экраны должны брать цвета через `LocalZavGarColors.current`.
 */
@Immutable
data class ZavGarColors(
    val accent: Color,
    val accentSoft: Color,
    val accentGlow: Color,
    val background: Color,
    val card: Color,
    val navBackground: Color,
    val foreground: Color,
    val foregroundSecondary: Color,
    val foregroundDisabled: Color,
    val border: Color,
    val inputBackground: Color,
    val success: Color,
    val successContainer: Color,
    val danger: Color,
    val dangerContainer: Color,
    val onAccent: Color,
    val isDark: Boolean,
)

val ZavGarLightColors = ZavGarColors(
    accent = Color(0xFFFF7021),
    accentSoft = Color(0xFFF7D7C5),
    accentGlow = Color(0x2EFF7021),
    background = Color(0xFFF5F5F5),
    card = Color(0xFFFFFFFF),
    navBackground = Color(0xFFF5F5F5),
    foreground = Color(0xFF333333),
    foregroundSecondary = Color(0xA6333333), // 0.65
    foregroundDisabled = Color(0x59333333),  // 0.35
    border = Color(0xFFD8DADC),
    inputBackground = Color(0xFFFFFFFF),
    success = Color(0xFF46A86E),
    successContainer = Color(0x1F46A86E),
    danger = Color(0xFFE64646),
    dangerContainer = Color(0x1AE64646),
    onAccent = Color(0xFFFFFFFF),
    isDark = false,
)

val ZavGarDarkColors = ZavGarColors(
    accent = Color(0xFFFF7021),
    accentSoft = Color(0x38FF7021), // 0.22
    accentGlow = Color(0x21FF7021), // 0.13
    background = Color(0xFF1A1A1A),
    card = Color(0xFF252525),
    navBackground = Color(0xFF1E1E1E),
    foreground = Color(0xFFF5F5F5),
    foregroundSecondary = Color(0x8CF5F5F5), // 0.55
    foregroundDisabled = Color(0x40F5F5F5),  // 0.25
    border = Color(0x1AFFFFFF),
    inputBackground = Color(0xFF2C2C2C),
    success = Color(0xFF46A86E),
    successContainer = Color(0x1F46A86E),
    danger = Color(0xFFE64646),
    dangerContainer = Color(0x1AE64646),
    onAccent = Color(0xFFFFFFFF),
    isDark = true,
)

val LocalZavGarColors = staticCompositionLocalOf { ZavGarLightColors }
