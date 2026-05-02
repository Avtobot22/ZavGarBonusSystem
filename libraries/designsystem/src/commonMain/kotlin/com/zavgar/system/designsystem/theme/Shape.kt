package com.zavgar.system.designsystem.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Карта скруглений ZavGar (соответствует прототипу `ZavGar App.html`):
 * - extraSmall  4dp   — мелкие чипы / pills
 * - small       12dp  — нав-айтемы, мелкие container'ы
 * - medium      14dp  — кнопки, поля ввода, OTP-боксы, иконки-плитки 13dp ≈ 14
 * - large       20dp  — карточки контента
 * - extraLarge  28dp  — нижний sheet / шапка-карточка с радиусом сверху
 */
val ZavGarShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Скругление "верхнего шита" — 28dp только сверху. */
val ZavGarTopSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

/** Скругление нижней нав-панели (20dp сверху). */
val ZavGarBottomNavShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
