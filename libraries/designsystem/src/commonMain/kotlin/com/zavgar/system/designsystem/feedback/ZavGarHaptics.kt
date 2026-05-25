package com.zavgar.system.designsystem.feedback

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Переиспользуемый источник тактильного отклика для всего приложения.
 *
 * Обёртка поверх [HapticFeedback] из Compose Multiplatform: мапит семантические
 * сценарии (успех/ошибка) на конкретные [HapticFeedbackType], доступные на обеих
 * платформах. Так вызывающий код не зависит от конкретных типов отклика.
 */
class ZavGarHaptics(
    private val hapticFeedback: HapticFeedback,
) {

    /** Отклик при успешном действии (подтверждение кода, сохранение и т.п.). */
    fun success() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
    }

    /** Отклик при ошибке/отклонении ввода (неверный код, валидация и т.п.). */
    fun error() {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.Reject)
    }
}

/**
 * Создаёт [ZavGarHaptics], привязанный к текущему [LocalHapticFeedback].
 */
@Composable
fun rememberZavGarHaptics(): ZavGarHaptics {
    val hapticFeedback = LocalHapticFeedback.current
    return remember(hapticFeedback) { ZavGarHaptics(hapticFeedback) }
}
