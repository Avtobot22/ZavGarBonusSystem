package com.zavgar.system.designsystem.theme

import androidx.compose.ui.graphics.Color

// --- ZAVGAR PRIMITIVES ---
val ZavgarOrange = Color(0xFFFF7021)
val ZavgarBlack = Color(0xFF333333)
val ZavgarWhite = Color(0xFFFFFFFF)
val ZavgarBackground = Color(0xFFF5F5F5)
val ZavgarStroke = Color(0xFFD8DADC)

val seed = ZavgarOrange

// --- LIGHT ---

val md_theme_light_primary = ZavgarOrange
val md_theme_light_onPrimary = ZavgarWhite
val md_theme_light_primaryContainer = Color(0xFFF7D7C5) // accentSoft
val md_theme_light_onPrimaryContainer = ZavgarBlack

val md_theme_light_secondary = ZavgarBlack
val md_theme_light_onSecondary = ZavgarWhite
val md_theme_light_secondaryContainer = ZavgarStroke
val md_theme_light_onSecondaryContainer = ZavgarBlack

val md_theme_light_tertiary = Color(0xFF46A86E) // success
val md_theme_light_onTertiary = ZavgarWhite
val md_theme_light_tertiaryContainer = Color(0x1F46A86E) // successContainer
val md_theme_light_onTertiaryContainer = Color(0xFF46A86E)

val md_theme_light_error = Color(0xFFE64646) // danger
val md_theme_light_onError = ZavgarWhite
val md_theme_light_errorContainer = Color(0x1AE64646) // dangerContainer
val md_theme_light_onErrorContainer = Color(0xFFE64646)

val md_theme_light_background = ZavgarBackground
val md_theme_light_onBackground = ZavgarBlack // foreground

val md_theme_light_surface = ZavgarWhite // card
val md_theme_light_onSurface = ZavgarBlack
val md_theme_light_surfaceVariant = ZavgarWhite // inputBackground
val md_theme_light_onSurfaceVariant = Color(0xA6333333) // foregroundSecondary
val md_theme_light_surfaceContainer = ZavgarBackground // navBackground

val md_theme_light_outline = ZavgarStroke // border
val md_theme_light_outlineVariant = Color(0x59333333) // foregroundDisabled

val md_theme_light_inverseOnSurface = ZavgarBackground
val md_theme_light_inverseSurface = ZavgarBlack

// --- DARK ---

val md_theme_dark_primary = ZavgarOrange
val md_theme_dark_onPrimary = ZavgarWhite
val md_theme_dark_primaryContainer = Color(0x38FF7021) // accentSoft dark
val md_theme_dark_onPrimaryContainer = Color(0xFFF5F5F5)

val md_theme_dark_secondary = Color(0xFFCCC2DC)
val md_theme_dark_onSecondary = Color(0xFF332D41)
val md_theme_dark_secondaryContainer = Color(0xFF4A4458)
val md_theme_dark_onSecondaryContainer = Color(0xFFE8DEF8)

val md_theme_dark_tertiary = Color(0xFF46A86E) // success
val md_theme_dark_onTertiary = ZavgarWhite
val md_theme_dark_tertiaryContainer = Color(0x1F46A86E) // successContainer
val md_theme_dark_onTertiaryContainer = Color(0xFF46A86E)

val md_theme_dark_error = Color(0xFFE64646) // danger
val md_theme_dark_onError = ZavgarWhite
val md_theme_dark_errorContainer = Color(0x1AE64646) // dangerContainer
val md_theme_dark_onErrorContainer = Color(0xFFE64646)

val md_theme_dark_background = Color(0xFF1A1A1A)
val md_theme_dark_onBackground = Color(0xFFF5F5F5) // foreground

val md_theme_dark_surface = Color(0xFF252525) // card
val md_theme_dark_onSurface = Color(0xFFF5F5F5)
val md_theme_dark_surfaceVariant = Color(0xFF2C2C2C) // inputBackground
val md_theme_dark_onSurfaceVariant = Color(0x8CF5F5F5) // foregroundSecondary
val md_theme_dark_surfaceContainer = Color(0xFF1E1E1E) // navBackground

val md_theme_dark_outline = Color(0x1AFFFFFF) // border
val md_theme_dark_outlineVariant = Color(0x40F5F5F5) // foregroundDisabled

val md_theme_dark_inverseOnSurface = Color(0xFF121212)
val md_theme_dark_inverseSurface = Color(0xFFE6E1E5)
