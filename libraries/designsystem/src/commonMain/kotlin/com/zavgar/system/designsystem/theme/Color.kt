package com.zavgar.system.designsystem.theme

import androidx.compose.ui.graphics.Color

// --- ZAVGAR PRIMITIVES (Базовые цвета из макета) ---
val ZavgarOrange = Color(0xFFFF7021)
val ZavgarBlack = Color(0xFF333333)
val ZavgarWhite = Color(0xFFFFFFFF)
val ZavgarBackground = Color(0xFFF5F5F5) // Светло-серый фон всего экрана
val ZavgarStroke = Color(0xFFD8DADC) // Цвет обводки инпутов

// Вычисленные цвета для состояний (контейнеры)
// Оранжевый 15% на белом фоне (для подложек)
val ZavgarOrangeContainer = Color(0xFFF7D7C5)

// Серый текст 70% от 333333 (для второстепенного текста)
val ZavgarTextSecondary = Color(0xFF707070)

// --- LIGHT THEME (Светлая тема - основная) ---

// Главные элементы (Кнопки, активные иконки)
val md_theme_light_primary = ZavgarOrange
val md_theme_light_onPrimary = ZavgarWhite // Текст на оранжевой кнопке

// Контейнеры (Слабый акцент, например, выделенная вкладка)
val md_theme_light_primaryContainer = ZavgarOrangeContainer
val md_theme_light_onPrimaryContainer = ZavgarBlack

// Вторичные элементы (Обычно иконки навигации, неактивные элементы)
val md_theme_light_secondary = ZavgarBlack
val md_theme_light_onSecondary = ZavgarWhite
val md_theme_light_secondaryContainer = ZavgarStroke // Используем серый как вторичный фон
val md_theme_light_onSecondaryContainer = ZavgarBlack

// Третичные цвета (Можно использовать для акцентов другого рода, например, успешные операции)
val md_theme_light_tertiary = Color(0xFF4CAF50) // Зеленый (пример для успеха) #F6E8E1
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFE8F5E9)
val md_theme_light_onTertiaryContainer = Color(0xFF1B5E20)

// Ошибки (Error)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)

// Фон и поверхности
val md_theme_light_background = ZavgarBackground // F5F5F5
val md_theme_light_onBackground = ZavgarBlack

val md_theme_light_surface = ZavgarWhite // Карточки, BottomSheet
val md_theme_light_onSurface = ZavgarBlack

// Поля ввода (Input Fields) часто используют SurfaceVariant
val md_theme_light_surfaceVariant = ZavgarWhite // Или F5F5F5, если поля серые внутри
val md_theme_light_onSurfaceVariant = ZavgarTextSecondary // Плейсхолдеры, лейблы

val md_theme_light_outline = ZavgarStroke // D8DADC
val md_theme_light_inverseOnSurface = ZavgarBackground
val md_theme_light_inverseSurface = ZavgarBlack

// --- DARK THEME (Темная тема - инверсия) ---
// Т.к. макета темной темы нет, создаем безопасную автоматическую инверсию.
// Оранжевый делаем светлее (Pastel Orange), фон - темным (но не черным).

val md_theme_dark_primary = Color(0xFFFFB784) // Более мягкий оранжевый
val md_theme_dark_onPrimary = Color(0xFF4E2600) // Темный текст на светлом оранжевом
val md_theme_dark_primaryContainer = Color(0xFF703800)
val md_theme_dark_onPrimaryContainer = Color(0xFFFFDCC1)

val md_theme_dark_secondary = Color(0xFFCCC2DC)
val md_theme_dark_onSecondary = Color(0xFF332D41)
val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)

val md_theme_dark_tertiary = Color(0xFFA5D6A7)
val md_theme_dark_onTertiary = Color(0xFF1B5E20)
val md_theme_dark_tertiaryContainer = Color(0xFF2E7D32)
val md_theme_dark_onTertiaryContainer = Color(0xFFC8E6C9)

val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)

val md_theme_dark_background = Color(0xFF121212)
val md_theme_dark_onBackground = Color(0xFFE6E1E5)

val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_onSurface = Color(0xFFE6E1E5)

val md_theme_dark_surfaceVariant = Color(0xFF49454F)
val md_theme_dark_onSurfaceVariant = Color(0xFFCAC4D0)
val md_theme_dark_outline = Color(0xFF938F99)
val md_theme_dark_inverseOnSurface = Color(0xFF121212)
val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)

val seed = ZavgarOrange