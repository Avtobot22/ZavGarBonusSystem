package com.zavgar.system.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// Semantic aliases over Material3 ColorScheme slots.
// Usage: MaterialTheme.colorScheme.accent / .card / .foreground …

val ColorScheme.accent: Color get() = primary
val ColorScheme.accentSoft: Color get() = primary.copy(alpha = 0.18f)
val ColorScheme.card: Color get() = surface
val ColorScheme.navBackground: Color get() = surfaceContainer
val ColorScheme.foreground: Color get() = onBackground
val ColorScheme.foregroundSecondary: Color get() = onSurfaceVariant
val ColorScheme.foregroundDisabled: Color get() = outlineVariant
val ColorScheme.border: Color get() = outline
val ColorScheme.inputBackground: Color get() = surfaceVariant
val ColorScheme.success: Color get() = tertiary
val ColorScheme.successContainer: Color get() = tertiaryContainer
val ColorScheme.danger: Color get() = error
val ColorScheme.dangerContainer: Color get() = errorContainer
val ColorScheme.onAccent: Color get() = onPrimary
